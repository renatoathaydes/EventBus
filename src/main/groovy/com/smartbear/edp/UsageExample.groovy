package com.smartbear.edp

// Example of how to use the EventManager

EventManager bus = new EventManager()

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

// sub1 will listen EventObject (not its sub-classes!) sent by sources of any type
def sub1Events = [ ]
EventSubscriber sub1 = [ handle: { e -> sub1Events << e } ] as EventSubscriber

// sub2 will only listen to SpecialEvents sent by sources of base-type A
def sub2Events = [ ]
EventSubscriber sub2 = [ handle: { e -> sub2Events << e } ] as EventSubscriber
sub2.sourceBaseType = A

// sub3 is more fussy than sub2 and wants only events sent by instances of B
def sub3Events = [ ]
EventSubscriber sub3 = [ handle: { e -> sub3Events << e } ] as EventSubscriber
sub3.sourceBaseType = B

// subscribe with the event bus
bus.subscribeWeak sub1, EventObject
bus.subscribeWeak sub2, SpecialGroovyEvent
bus.subscribeWeak sub3, SpecialGroovyEvent

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
assert sub3Events[0].class == SpecialGroovyEvent
assert sub3Events[0].source.class == B
assert sub3Events[0].source.value == 2
assert sub3Events[0].source.name == 'John'

println 'All done!'
