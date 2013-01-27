package com.smartbear.edp.testosgiapp

import com.smartbear.edp.api.EventManager
import com.smartbear.edp.testosgiapp.swing.AppGui
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * A simple example on setting up and starting a Spring-OSGi app.
 * Based on this:
 * http://java.dzone.com/news/osgi-and-spring-dynamic
 * User: Renato
 */
class TestOsgiApp {

	static final Logger log = LoggerFactory.getLogger( TestOsgiApp.class )

	final gui

	TestOsgiApp( EventManager manager ) {
		log.info stars() + "\nEventManager's class hierarchy: " +
				classHierarchy( manager.class ).reverse()
		gui = new AppGui( manager )
	}

	def stars( ) { '*' * 50 }

	def classHierarchy( Class clazz, acc = [] ) {
		if ( clazz ) {
			acc << clazz.name
			classHierarchy clazz.superclass, acc
		}
		return acc
	}

	void start( ) {
		log.info stars() + "\nStarting TestOsgiApp"
		gui.show()
	}

	void stop( ) {
		log.info stars() + "\nStopping TestOsgiApp"
		gui.hide()
	}

}
