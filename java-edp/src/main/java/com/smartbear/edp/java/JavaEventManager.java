package com.smartbear.edp.java;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.smartbear.edp.api.EventManager;
import com.smartbear.edp.api.EventSubscriber;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * User: Renato
 */
public class JavaEventManager implements EventManager {

	/**
	 * Helper class which transparently keeps either a weak or strong reference
	 * to a subscriber.
	 */
	private class SubscriberRef {
		WeakReference<EventSubscriber<?>> weakRef;
		EventSubscriber<?> strongRef;
		boolean isStrongRef;
		SubscriberRef(EventSubscriber<?> subscriber, boolean isStrongRef) {
			this.isStrongRef = isStrongRef;
			if (isStrongRef) strongRef = subscriber;
			else weakRef = new WeakReference<EventSubscriber<?>>( subscriber );
		}
		EventSubscriber<?> get() {
			return isStrongRef ? strongRef : weakRef.get();
		}
	}

	private EventBus bus = new EventBus();
	Map<Class<?>, List<SubscriberRef>> handlers = new HashMap<>();


	public JavaEventManager() {
		bus.register( this );
	}

	@Subscribe
	public void onEvent( EventObject event ) {
		List<SubscriberRef> deadRefs = new ArrayList<>();
		List<SubscriberRef> refs = handlers.get( event.getClass() );
		if ( refs != null ) {
			for ( SubscriberRef ref : refs ) {
				EventSubscriber subscriber = ref.get();
				if ( subscriber != null ) {
					if ( subscriber.getSourceBaseType().isAssignableFrom( event.getSource().getClass() ) )
						subscriber.handle( event );
				} else {
					deadRefs.add( ref );
				}
			}
			refs.removeAll( deadRefs );
		}
	}

	@Override
	public <K extends EventObject> void subscribe( EventSubscriber<K> subscriber, Class<K> eventType ) {
		doSubscribe( subscriber, eventType, true );
	}

	@Override
	public <K extends EventObject> void subscribeWeakly( EventSubscriber<K> subscriber, Class<K> eventType ) {
		doSubscribe( subscriber, eventType, false );
	}

	private <K extends EventObject> void doSubscribe( EventSubscriber<K> subscriber,
	           Class<K> eventType, boolean isStrongRef ) {
		List<SubscriberRef> refs = handlers.get( eventType );
		if ( refs == null ) {
			refs = new ArrayList<>();
			handlers.put( eventType, refs );
		}
		refs.add( new SubscriberRef( subscriber, isStrongRef ) );
	}

	@Override
	public <K extends EventObject> void unSubscribe( EventSubscriber<K> subscriber ) {
		for ( List<SubscriberRef> refs : handlers.values() ) {
			for ( Iterator<SubscriberRef> it = refs.iterator(); it.hasNext();) {
				if ( subscriber == it.next().get() ) {
					it.remove();
					return;
				}
			}
		}
	}

	public void post( EventObject event ) {
		bus.post( event );
	}

	@Override
	public String getName() {
		return JavaEventManager.class.getName();
	}

}


