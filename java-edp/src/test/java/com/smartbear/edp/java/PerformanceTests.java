package com.smartbear.edp.java;

import com.smartbear.edp.api.EventManager;
import com.smartbear.edp.api.EventSubscriber;
import com.smartbear.edp.api.EventSubscriberBase;
import com.smartbear.edp.groovy.GroovyEventManager;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Renato
 */
public class PerformanceTests extends TestCase {

	public void testGarbageCollectedSubscribersAreUnregistered() {
		JavaEventManager manager = new JavaEventManager();

		for ( int i = 0; i < 1000; i++ ) {
			manager.subscribeWeakly( new EventSubscriberBase<EventObject>() {
				public void handle( EventObject event ) {
				}
			}, EventObject.class );
		}

		forceGC();
		// must post an event for the manager to get rid of weakRefs
		manager.post( new EventObject( "" ) );

		Assert.assertEquals( 0, manager.handlers.get( EventObject.class ).size() );

		List<EventSubscriber<EventObject>> strongRefs = new ArrayList<>();
		for ( int i = 0; i < 1000; i++ ) {
			strongRefs.add( new EventSubscriberBase<EventObject>() {
				public void handle( EventObject event ) {
				}
			} );
		}
		for ( EventSubscriber<EventObject> ref : strongRefs ) {
			manager.subscribeWeakly( ref, EventObject.class );
		}
		forceGC();
		// must post an event for the manager to get rid of weakRefs
		manager.post( new EventObject( "" ) );

		Assert.assertEquals( 1000, manager.handlers.get( EventObject.class ).size() );

	}

	enum Language {JAVA, GROOVY}

	public void testJavaGroovyPerformances() {
		final int HANDLERS_COUNT = 100;
		final int EVENTS = 1000;
		final int TEST_RUNS = 100;

		System.out.println(
				"***** Starting tests with settings: *****\n" +
						"  Subscribers:     " + HANDLERS_COUNT + "\n" +
						"  Events posted:   " + EVENTS + "\n" +
						"  Number of runs:  " + TEST_RUNS + "\n" +
						"*****************************************" );
		doPerformanceTestWith( Language.JAVA, HANDLERS_COUNT, EVENTS, TEST_RUNS );
		doPerformanceTestWith( Language.GROOVY, HANDLERS_COUNT, EVENTS, TEST_RUNS );
		doPerformanceTestWith( Language.GROOVY, HANDLERS_COUNT, EVENTS, TEST_RUNS );
		doPerformanceTestWith( Language.JAVA, HANDLERS_COUNT, EVENTS, TEST_RUNS );

	}


	private void doPerformanceTestWith( Language lang, int HANDLERS_COUNT, int EVENTS, int TEST_RUNS ) {
		List<PerformanceResults> results = new ArrayList<>();

		// throw away first performance results
		checkPerformance( lang, HANDLERS_COUNT, EVENTS, 10 );

		long totalTime = 0;
		long minTime = Integer.MAX_VALUE;
		long maxTime = 0;

		for ( int i = 0; i < TEST_RUNS; i++ ) {
			results.add( checkPerformance( lang, HANDLERS_COUNT, EVENTS, 10 ) );
		}
		Assert.assertEquals( TEST_RUNS, results.size() );

		for ( PerformanceResults result : results ) {
			Assert.assertEquals( EVENTS * HANDLERS_COUNT, result.count );
			totalTime += result.time;
			minTime = Math.min( result.time, minTime );
			maxTime = Math.max( result.time, maxTime );
		}
		System.out.println( lang + " Results: min: " + minTime +
				", max: " + maxTime + ", avg: " + ( totalTime / results.size() ) );
	}

	private PerformanceResults checkPerformance( final Language lang,
	                                             final int HANDLERS_COUNT,
	                                             final int EVENTS, final int timeout ) {

		EventManager java = new JavaEventManager();
		EventManager groovy = new GroovyEventManager();

		final FutureTask<Integer> finish = new FutureTask<>( new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 1;
			}
		} );
		final AtomicInteger count = new AtomicInteger();

		// need to keep references to all subscribers so they are not garbage collected
		List<EventSubscriber<EventObject>> javaRefs = new ArrayList<>();
		List<EventSubscriber<EventObject>> groovyRefs = new ArrayList<>();

		if ( lang == Language.JAVA ) {
			for ( int i = 0; i < HANDLERS_COUNT; i++ ) {
				javaRefs.add( new EventSubscriberBase<EventObject>() {
					public void handle( EventObject event ) {
						int c = count.incrementAndGet();
						if ( c == HANDLERS_COUNT * EVENTS ) {
							finish.run();
						}
					}
				} );
			}
			for ( EventSubscriber<EventObject> subscriber : javaRefs ) {
				java.subscribeWeakly( subscriber, EventObject.class );
			}
		} else {
			for ( int i = 0; i < HANDLERS_COUNT; i++ ) {
				groovyRefs.add( new EventSubscriberBase<EventObject>() {
					public void handle( EventObject event ) {
						int c = count.incrementAndGet();
						if ( c == HANDLERS_COUNT * EVENTS ) {
							finish.run();
						}
					}
				} );
			}
			for ( EventSubscriber<EventObject> subscriber : groovyRefs ) {
				groovy.subscribeWeakly( subscriber, EventObject.class );
			}
		}

		long end = 0;
		long start = System.currentTimeMillis();
		for ( int i = 0; i < EVENTS; i++ ) {
			switch ( lang ) {
				case JAVA:
					java.post( new EventObject( "" ) );
					break;
				case GROOVY:
					groovy.post( new EventObject( "" ) );
			}
		}

		try {
			finish.get( timeout, TimeUnit.SECONDS );
			end = System.currentTimeMillis();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		// ensure no references were lost
		Assert.assertEquals( HANDLERS_COUNT, lang == Language.JAVA ? javaRefs.size() : groovyRefs.size() );

		javaRefs.size();
		PerformanceResults res = new PerformanceResults();
		res.time = end - start;
		res.count = count.get();
		return res;

	}

	private class PerformanceResults {
		long time;
		long count;
	}

	static void forceGC() {
		WeakReference weakRef = new WeakReference( new Object() );
		while ( weakRef.get() != null ) {
			String[] str = new String[99999];
		}
		System.out.println( "GC Performed" );
	}
}
