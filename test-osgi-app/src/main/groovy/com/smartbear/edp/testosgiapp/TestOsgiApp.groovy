package com.smartbear.edp.testosgiapp

import com.smartbear.edp.api.EventManager

/**
 * A simple example on setting up and starting a Spring-OSGi app.
 * Based on this:
 * http://java.dzone.com/news/osgi-and-spring-dynamic
 * User: Renato
 */
class TestOsgiApp {

	TestOsgiApp( EventManager manager ) {
		stars()
		println "Got the EventManager: ${manager.class.name}"
		stars()
	}

	def stars( ) { println '*' * 50 }

	void start( ) {
		stars()
		println 'Starting TestOsgiApp'
	}

	void stop( ) {
		stars()
		println 'Stopping TestOsgiApp'
	}

}
