package com.smartbear.edp.fxmonitor

import javafx.application.Application
import javafx.scene.layout.HBox
import org.junit.Test
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleEvent
import org.osgi.framework.BundleListener

/**
 *
 * User: Renato
 */
class FXMonitorAppTest {

	@Test
	void testGui( ) {
		def listeners = [ ]
		def mockCtx = [
				addBundleListener: { BundleListener listener ->
					listeners << listener
				},
				getBundle: { _ -> null }
		] as BundleContext
		FXMonitorApp.context = mockCtx
		FXMonitorApp.monitorBundles()

		assert listeners.size() == 1

		Thread.start {
			Application.launch FXMonitorApp
		}

		sleep 250

		def mockBundle = [
				getSymbolicName: { 'aBundle' }
		] as Bundle

		listeners[ 0 ].bundleChanged new BundleEvent( BundleEvent.STARTED, mockBundle )

		sleep 250

		HBox hbox = FXMonitorApp.bundleInfoContainer.children[ 0 ]
		assert hbox.children[ 0 ].text == 'aBundle'
		assert hbox.children[ 1 ].text == 'Started'

		sleep 10000

	}

}
