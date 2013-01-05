package com.smartbear.edp;

import com.smartbear.edp.JavaEventManager.JavaEventSubscriber;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Renato
 */
public class JavaEventManagerTest extends TestCase {

	public void testGarbageCollectedSubscribersAreUnregistered() {
		JavaEventManager manager = new JavaEventManager();

		for ( int i = 0; i < 1000; i++ ) {
			manager.subscribeWeak( new JavaEventSubscriber<EventObject>() {
				public void handle( EventObject event ) {
				}
			}, EventObject.class );
		}

		EventManagerTest.forceGC();
		// must post an event for the manager to get rid of weakRefs
		manager.post( new EventObject( "" ) );

		assertEquals( 0, manager.handlers.get( EventObject.class ).size() );

		List<JavaEventSubscriber<EventObject>> strongRefs = new ArrayList<>();
		for ( int i = 0; i < 1000; i++ ) {
			strongRefs.add( new JavaEventSubscriber<EventObject>() {
				public void handle( EventObject event ) {
				}
			} );
		}
		for ( JavaEventSubscriber<EventObject> ref : strongRefs ) {
			manager.subscribeWeak( ref, EventObject.class );
		}
		EventManagerTest.forceGC();
		// must post an event for the manager to get rid of weakRefs
		manager.post( new EventObject( "" ) );

		assertEquals( 1000, manager.handlers.get( EventObject.class ).size() );

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
		assertEquals( TEST_RUNS, results.size() );

		for ( PerformanceResults result : results ) {
			assertEquals( EVENTS * HANDLERS_COUNT, result.count );
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

		JavaEventManager java = new JavaEventManager();
		EventManager groovy = new EventManager();

		final FutureTask<Integer> finish = new FutureTask<>( new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return 1;
			}
		} );
		final AtomicInteger count = new AtomicInteger();

		// need to keep references to all subscribers so they are not garbage collected
		List<JavaEventSubscriber<EventObject>> javaRefs = new ArrayList<>();
		List<EventSubscriber<EventObject>> groovyRefs = new ArrayList<>();

		if ( lang == Language.JAVA ) {
			for ( int i = 0; i < HANDLERS_COUNT; i++ ) {
				javaRefs.add( new JavaEventSubscriber<EventObject>() {
					public void handle( EventObject event ) {
						int c = count.incrementAndGet();
						if ( c == HANDLERS_COUNT * EVENTS ) {
							finish.run();
						}
					}
				} );
			}
			for ( JavaEventSubscriber<EventObject> subscriber : javaRefs ) {
				java.subscribeWeak( subscriber, EventObject.class );
			}
		} else {
			for ( int i = 0; i < HANDLERS_COUNT; i++ ) {
				groovyRefs.add( new EventSubscriber<EventObject>() {
					public void handle( EventObject event ) {
						int c = count.incrementAndGet();
						if ( c == HANDLERS_COUNT * EVENTS ) {
							finish.run();
						}
					}
				} );
			}
			for ( EventSubscriber<EventObject> subscriber : groovyRefs ) {
				groovy.subscribeWeak( subscriber, EventObject.class );
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
		assertEquals(HANDLERS_COUNT, lang == Language.JAVA ? javaRefs.size() : groovyRefs.size() );

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


}
