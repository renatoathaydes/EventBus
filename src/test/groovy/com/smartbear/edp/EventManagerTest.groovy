package com.smartbear.edp

import java.lang.ref.WeakReference
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

/**
 *
 * User: Renato
 */
class EventManagerTest extends GroovyTestCase {

	private class SomeEvent extends EventObject {
		SomeEvent( source ) { super( source ) }
	}

	private class OtherEvent extends EventObject {
		OtherEvent( source ) { super( source ) }
	}

	void testCanSendAndReceiveEvents( ) {
		EventManager manager = new EventManager()
		FutureTask ft1 = new FutureTask( { true } )
		FutureTask ft2 = new FutureTask( { true } )
		FutureTask ft3 = new FutureTask( { true } )
		def subscriber1 = [ handle: { EventObject e -> ft1.run() } ] as EventSubscriber
		def subscriber2 = [ handle: { EventObject e -> ft2.run() } ] as EventSubscriber
		def subscriber3 = [ handle: { EventObject e -> ft3.run() } ] as EventSubscriber
		manager.subscribeWeak subscriber1, EventObject
		manager.subscribeWeak subscriber2, EventObject
		manager.subscribeWeak subscriber3, EventObject
		manager.post new EventObject( 'hello' )
		assertTrue ft1.get( 100, TimeUnit.MILLISECONDS )
		assertTrue ft2.get( 100, TimeUnit.MILLISECONDS )
		assertTrue ft3.get( 100, TimeUnit.MILLISECONDS )
	}

	void testEventSourceBaseTypeIsRespected( ) {
		EventManager manager = new EventManager()
		FutureTask ft1 = new FutureTask( { true } )
		FutureTask ft2 = new FutureTask( { true } )
		def subscriber1 = [ handle: { EventObject e -> ft1.run() } ] as EventSubscriber
		subscriber1.sourceBaseType = String
		def subscriber2 = [ handle: { EventObject e -> ft2.run() } ] as EventSubscriber
		subscriber2.sourceBaseType = Boolean
		manager.subscribeWeak subscriber1, EventObject
		manager.subscribeWeak subscriber2, EventObject
		manager.post new EventObject( 'hello' )
		assertTrue ft1.get( 100, TimeUnit.MILLISECONDS )
		shouldFail { ft2.get( 100, TimeUnit.MILLISECONDS ) }
	}

	void testDoesNotReceiveDifferentClassEvents( ) {
		EventManager manager = new EventManager()
		FutureTask ft = new FutureTask( { true } )
		def subscriber = [ handle: { SomeEvent e -> ft.run() } ] as EventSubscriber
		manager.subscribeWeak( subscriber, SomeEvent )

		def ft2 = new FutureTask( { true } )
		def subscriber2 = [ handle: { OtherEvent e -> ft2.run() } ] as EventSubscriber
		manager.subscribeWeak( subscriber2, OtherEvent )

		manager.post( new EventObject( 'will not be used' ) )
		shouldFail { ft.get( 100, TimeUnit.MILLISECONDS ) }
		shouldFail { ft2.get( 10, TimeUnit.MILLISECONDS ) }
	}

	void testCorrectHandlersGetEventsByType( ) {
		EventManager manager = new EventManager()

		FutureTask boolTask = new FutureTask( { true } )
		FutureTask strTask = new FutureTask( { 'str' } )
		def count = 0
		def objTask = { ++count }

		def boolSubscriber = [ handle: { OtherEvent e -> boolTask.run() } ] as EventSubscriber
		manager.subscribeWeak( boolSubscriber, OtherEvent )
		def strSubscriber = [ handle: { SomeEvent e -> strTask.run() } ] as EventSubscriber
		manager.subscribeWeak( strSubscriber, SomeEvent )

		// class hierarchy does not matter, hence an EventObject handler should receive only
		// events of class EventObject
		def objSubscriber = [ handle: { EventObject e -> objTask() } ] as EventSubscriber
		manager.subscribeWeak objSubscriber, EventObject

		manager.post new SomeEvent( 'some' )
		manager.post new OtherEvent( 'other' )
		3.times { manager.post new EventObject( 'event' ) }

		assertEquals true, boolTask.get( 100, TimeUnit.MILLISECONDS )
		assertEquals 'str', strTask.get( 100, TimeUnit.MILLISECONDS )
		assertEquals 3, count

	}

	void testGarbageCollectedSubscribersAreUnregistered( ) {
		EventManager manager = new EventManager()

		1000.times {
			manager.subscribeWeak( new EventSubscriber() {
				void handle( EventObject event ) {}
			}, EventObject )
		}
		forceGC()
		// must post an event for the manager to get rid of weakRefs
		manager.post new EventObject( "" )

		assertEquals 0, manager.handlers[ EventObject ].size()

		def strongRefs = [ ]
		1000.times {
			strongRefs << new EventSubscriber() {
				void handle( EventObject event ) {}
			}
		}
		strongRefs.each {
			manager.subscribeWeak( it, EventObject )
		}
		forceGC()
		// must post an event for the manager to get rid of weakRefs
		manager.post new EventObject( "" )

		assertEquals 1000, manager.handlers[ EventObject ].size()

	}

	static void forceGC( ) {
		WeakReference weakRef = new WeakReference( new Object() );
		while ( weakRef.get() ) {
			new String[99999]
		}
		println 'GC Performed'
	}

}
