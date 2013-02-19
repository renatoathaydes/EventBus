package com.smartbear.edp.fxmonitor

import org.junit.Test

/**
 *
 * User: Renato
 */
class FXMonitorTest {

	@Test
	void testFX( ) {
		def monitor = new FXMonitor()
		monitor.start()

		sleep 10000
	}
}
