package com.smartbear.edp.groovy

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

import com.smartbear.edp.api.EventManager
import com.smartbear.edp.api.EventSubscriber

import java.lang.ref.WeakReference

/**
 *
 * User: Renato
 */
class GroovyEventManager implements EventManager {

	private EventBus bus = new EventBus()
	protected handlers = [:]

	GroovyEventManager() {
		bus.register( this )
	}

	@Subscribe
	void onEvent( event ) {
		def deadRefs = []
		def handler = handlers[event.class]
		handler?.each { ref ->
			def subscriber = ref.get()
			if (subscriber) {
				if ( subscriber.sourceBaseType.isAssignableFrom( event.source.class ) )
					subscriber.handle( event )
			} else {
				deadRefs << ref
			}
		}
		handler?.removeAll( deadRefs )
	}

	@Override
	void post( EventObject event) {
		bus.post( event )
	}

	@Override
	def <K extends EventObject> void subscribe( EventSubscriber<K> subscriber, Class<K> eventType ) {
		handlers.get(eventType, []) << subscriber
	}

	@Override
	def <K extends EventObject> void subscribeWeakly( EventSubscriber<K> subscriber, Class<K> eventType ) {
		handlers.get(eventType, []) << new WeakReference( subscriber )
	}

	@Override
	def <K extends EventObject> void unSubscribe( EventSubscriber<K> subscriber ) {
		handlers.values().each { it.remove subscriber }
	}
}