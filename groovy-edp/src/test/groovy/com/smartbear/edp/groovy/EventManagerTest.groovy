package com.smartbear.edp.groovy

import com.smartbear.edp.api.EventManager
import com.smartbear.edp.api.EventSubscriber
import com.smartbear.edp.api.EventSubscriberBase
import com.smartbear.edp.groovy.GroovyEventManager

/**
 * Groovy TestCase's class names must end in 'Test' for Maven to automatically run them
 * User: Renato
 */
class EventManagerTest extends GroovyTestCase {

	// define some custom types
	class SpecialGroovyEvent extends EventObject {
		SpecialGroovyEvent( source ) { super( source ) }
	}
	class A {
		def value
	}
	class B extends A {
		def name
	}

	void testUsageWithWeakRefs( ) {
		// Example of how to use the EventManager
		EventManager bus = new GroovyEventManager()

		// sub1 will listen EventObject (not its sub-classes!) sent by sources of any type
		def sub1Events = [ ]
		EventSubscriber sub1 = [ handle: { e -> sub1Events << e } ] as EventSubscriberBase

		// sub2 will only listen to SpecialEvents sent by sources of base-type A
		def sub2Events = [ ]
		EventSubscriber sub2 = [ handle: { e -> sub2Events << e } ] as EventSubscriberBase
		sub2.sourceBaseType = A

		// sub3 is more fussy than sub2 and wants only events sent by instances of B
		def sub3Events = [ ]
		EventSubscriber sub3 = [ handle: { e -> sub3Events << e } ] as EventSubscriberBase
		sub3.sourceBaseType = B

		// subscribe weakly with the event bus
		// (so the subscriber will be garbage-collected if only the EventManager, and no one else, knows about it)
		bus.subscribeWeakly sub1, EventObject
		bus.subscribeWeakly sub2, SpecialGroovyEvent
		bus.subscribeWeakly sub3, SpecialGroovyEvent

		// send some events
		bus.post new EventObject( 'normal sender' )                // sub1 will get this
		bus.post new EventObject( true )                           // sub1 gets this also
		bus.post new SpecialGroovyEvent( 'No subscribers for this one' ) // this will be lost
		bus.post new SpecialGroovyEvent( new A( value: 1 ) )               // sub2 gets this
		bus.post new SpecialGroovyEvent( new B( value: 2, name: 'John' ) ) // sub2 gets this, and sub3 too

		// let the events be handled
		sleep 100

		// ensure all events we subscribed to were received
		assert sub1Events.size() == 2
		sub1Events.each { event -> assert event.class == EventObject }
		assert sub1Events[ 0 ].source == 'normal sender'
		assert sub1Events[ 1 ].source == true

		assert sub2Events.size() == 2
		sub2Events.each { event -> assert event.class == SpecialGroovyEvent }
		assert sub2Events[ 0 ].source.class == A
		assert sub2Events[ 0 ].source.value == 1
		assert sub2Events[ 1 ].source.class == B
		assert sub2Events[ 1 ].source.value == 2
		assert sub2Events[ 1 ].source.name == 'John'

		assert sub3Events.size() == 1
		assert sub3Events[ 0 ].class == SpecialGroovyEvent
		assert sub3Events[ 0 ].source.class == B
		assert sub3Events[ 0 ].source.value == 2
		assert sub3Events[ 0 ].source.name == 'John'

		println 'All done!'

	}

	void testUsageWithStrongRefs( ) {
		// Example of how to use the EventManager
		EventManager bus = new GroovyEventManager()

		// sub1 will listen EventObject (not its sub-classes!) sent by sources of any type
		def sub1Events = [ ]
		EventSubscriber sub1 = [ handle: { e -> sub1Events << e } ] as EventSubscriberBase

		// sub2 will only listen to SpecialEvents sent by sources of base-type A
		def sub2Events = [ ]
		EventSubscriber sub2 = [ handle: { e -> sub2Events << e } ] as EventSubscriberBase
		sub2.sourceBaseType = A

		// sub3 is more fussy than sub2 and wants only events sent by instances of B
		def sub3Events = [ ]
		EventSubscriber sub3 = [ handle: { e -> sub3Events << e } ] as EventSubscriberBase
		sub3.sourceBaseType = B

		// subscribe strongly with the event bus
		// (so the subscriber will never be garbage-collected)
		// To stop listening on events, the user must unregister the subscriber
		bus.subscribe sub1, EventObject
		bus.subscribe sub2, SpecialGroovyEvent
		bus.subscribe sub3, SpecialGroovyEvent

		// send some events
		bus.post new EventObject( 'normal sender' )                // sub1 will get this
		bus.post new EventObject( true )                           // sub1 gets this also
		bus.post new SpecialGroovyEvent( 'No subscribers for this one' ) // this will be lost
		bus.post new SpecialGroovyEvent( new A( value: 1 ) )               // sub2 gets this
		bus.post new SpecialGroovyEvent( new B( value: 2, name: 'John' ) ) // sub2 gets this, and sub3 too

		// let the events be handled
		sleep 100

		// ensure all events we subscribed to were received
		assert sub1Events.size() == 2
		sub1Events.each { event -> assert event.class == EventObject }
		assert sub1Events[ 0 ].source == 'normal sender'
		assert sub1Events[ 1 ].source == true

		assert sub2Events.size() == 2
		sub2Events.each { event -> assert event.class == SpecialGroovyEvent }
		assert sub2Events[ 0 ].source.class == A
		assert sub2Events[ 0 ].source.value == 1
		assert sub2Events[ 1 ].source.class == B
		assert sub2Events[ 1 ].source.value == 2
		assert sub2Events[ 1 ].source.name == 'John'

		assert sub3Events.size() == 1
		assert sub3Events[ 0 ].class == SpecialGroovyEvent
		assert sub3Events[ 0 ].source.class == B
		assert sub3Events[ 0 ].source.value == 2
		assert sub3Events[ 0 ].source.name == 'John'

		println 'All done!'

	}

}
