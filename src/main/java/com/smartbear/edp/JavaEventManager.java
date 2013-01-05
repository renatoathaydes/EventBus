package com.smartbear.edp;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * User: Renato
 */
public class JavaEventManager {

	private EventBus bus = new EventBus();
	Map<Class<?>, List<WeakReference<JavaEventSubscriber<?>>>> handlers =
			new HashMap<>();

	public JavaEventManager() {
		bus.register( this );
	}

	@Subscribe
	public void onEvent( EventObject event ) {
		List<WeakReference<JavaEventSubscriber<?>>> deadRefs = new ArrayList<>();
		List<WeakReference<JavaEventSubscriber<?>>> refs = handlers.get( event.getClass() );
		if ( refs != null ) {
			for ( WeakReference<JavaEventSubscriber<?>> ref : refs ) {
				JavaEventSubscriber subscriber = ref.get();
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

	public <K extends EventObject> void subscribeWeak( JavaEventSubscriber<K> subscriber, Class<K> eventType ) {
		List<WeakReference<JavaEventSubscriber<?>>> refs = handlers.get( eventType );
		if ( refs == null ) {
			refs = new ArrayList<>();
			handlers.put( eventType, refs );
		}
		refs.add( new WeakReference( subscriber ) );
	}

	public void post( EventObject event ) {
		bus.post( event );
	}

	public static abstract class JavaEventSubscriber<K extends EventObject> {
		private Class<?> sourceBaseType = Object.class;

		public abstract void handle( K event );

		public Class<?> getSourceBaseType() {
			return sourceBaseType;
		}

		public void setSourceBaseType( Class<?> sourceBaseType ) {
			this.sourceBaseType = sourceBaseType;
		}

	}

}


