package com.smartbear.edp

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

import java.lang.ref.WeakReference

/**
 *
 * User: Renato
 */
class EventManager {

	private EventBus bus = new EventBus()
	protected handlers = [:]

	EventManager( ) {
		bus.register( this )
	}

	@Subscribe
	void onEvent( event ) {
		def deadRefs = []
		handlers[event.class]?.each { ref ->
			def subscriber = ref.get()
			if (subscriber) {
				if ( subscriber.sourceBaseType.isAssignableFrom( event.source.class ) )
					subscriber.handle( event )
			} else {
				deadRefs << ref
			}
		}
		handlers[event.class]?.removeAll( deadRefs )
	}

	void subscribeWeak( EventSubscriber subscriber, Class<? extends EventObject> eventType ) {
		handlers.get(eventType, []) << new WeakReference( subscriber )
	}

	void post( EventObject event) {
		bus.post( event )
	}

}

abstract class EventSubscriber<K extends EventObject> {
	Class sourceBaseType = Object
	abstract void handle(K event)
}