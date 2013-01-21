package com.smartbear.edp.java;

import com.smartbear.edp.api.EventSubscriberBase;
import org.junit.Test;

import java.util.EventObject;

/**
 * User: Renato
 */
public class EventManagerTest {
	//TODO implement

	public static class JavaEventSubscriber<K extends EventObject>
			extends EventSubscriberBase<K> {
		public void handle( K event ) {
			//TODO
		}
	}

	@Test
	public void testWeakly() {
		//TODO
	}

	@Test
	public void testStrongly() {
		//TODO
	}

}
