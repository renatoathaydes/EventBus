package com.smartbear.edp.impl;

import com.smartbear.edp.EventManager;
import com.smartbear.edp.EventSubscriber;
import junit.framework.TestCase;
import com.smartbear.edp.Bean;

import java.lang.ref.WeakReference;
import java.util.EventObject;

public class BeanImplTest extends TestCase {

	public void testBeanIsABean() {
		Bean aBean = new BeanImpl();
		assertTrue( aBean.isABean() );
	}


}