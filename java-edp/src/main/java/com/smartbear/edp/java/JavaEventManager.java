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

	private EventBus bus = new EventBus();
	Map<Class<?>, List<WeakReference<EventSubscriber<?>>>> handlers =
			new HashMap<>();

	public JavaEventManager() {
		bus.register( this );
	}

	@Subscribe
	public void onEvent( EventObject event ) {
		List<WeakReference<EventSubscriber<?>>> deadRefs = new ArrayList<>();
		List<WeakReference<EventSubscriber<?>>> refs = handlers.get( event.getClass() );
		if ( refs != null ) {
			for ( WeakReference<EventSubscriber<?>> ref : refs ) {
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
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <K extends EventObject> void subscribeWeakly( EventSubscriber<K> subscriber, Class<K> eventType ) {
		List<WeakReference<EventSubscriber<?>>> refs = handlers.get( eventType );
		if ( refs == null ) {
			refs = new ArrayList<>();
			handlers.put( eventType, refs );
		}
		refs.add( new WeakReference( subscriber ) );
	}

	private <K extends EventObject> void doSubscribe( EventSubscriber<K> subscriber, Class<K> eventType ) {
		//TODO implement
	}

	@Override
	public <K extends EventObject> void unSubscribe( EventSubscriber<K> subscriber ) {
		//TODO implement
	}

	public void post( EventObject event ) {
		bus.post( event );
	}

}


