package com.smartbear.edp.fxmonitor

import javafx.application.Platform
import org.osgi.framework.BundleContext

/**
 *
 * User: Renato
 */
class FXMonitor {

	void start( ) {
		println 'Launching FXMonitorApp!'
		FXMonitorApp.launchIt()
		println 'Application launched'
	}

	void stop( ) {
		println 'Stopping FXMonitorApp, asking JavaFX Platform to exit'
		Platform.exit()
	}

	void setContext( BundleContext context ) {
		FXMonitorApp.context = context
		FXMonitorApp.monitorBundles()
	}

}
