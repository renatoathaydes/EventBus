package com.smartbear.edp.testosgiapp

import com.smartbear.edp.testosgiapp.swing.AppGui
//import org.osgi.framework.FrameworkUtil
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

	AppGui gui

	TestOsgiApp( ) {
		log.info 'Creating TestOsgiApp instance'
	}

	static stars( ) { '*' * 25 }

	void setGui( AppGui gui ) {
		println "Injecting gui into TestOsgiApp"
		this.gui = gui
		//FIXME onClose does not seem to work
		//gui.onClose = { FrameworkUtil.getBundle( this.class )?.bundleContext?.getBundle( 0 )?.stop() }
		log.info 'Showing the GUI'
		gui.show()
	}

	void start( ) {
		log.info "${stars()} Starting TestOsgiApp ${stars()}"
	}

	void stop( ) {
		log.info "${stars()} Stopping TestOsgiApp ${stars()}"
		gui?.hide()
	}

}
