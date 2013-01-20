package com.smartbear.edp.java;

import java.util.EventObject;

/**
 * User: Renato
 */
public class EventManagerTest {
	//TODO implement

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
