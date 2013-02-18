package com.smartbear.edp.fxmonitor

import javafx.application.Application
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.stage.Stage
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
	}

}


public class FXMonitorApp extends Application {

	static BundleContext context
	final bundleNamesNode = new TextArea()

	@Override
	public void start( Stage primaryStage ) throws Exception {
		primaryStage.title = 'OSGi Monitor Application'

		bundleNamesNode.wrapText = true

		Button btn = new Button()
		btn.text = "Test"
		btn.onAction = [
				handle: { ActionEvent event ->
					println "Hello World!"
				}
		] as EventHandler

		VBox root = new VBox()
		root.spacing = 20
		root.children.addAll( bundleNamesNode, btn )
		primaryStage.scene = new Scene( root, 300, 250 )
		primaryStage.show()

		monitorBundles()
	}

	void monitorBundles( ) {
		def timer = new Timer()
		timer.scheduleAtFixedRate( [ run: {
			Platform.runLater( [ run: {
				def names = context ?
					context.bundles*.symbolicName :
					'BundleContext not set'
				if ( names != bundleNamesNode.text )
					bundleNamesNode.text = names
			} ] as Runnable )
		} ] as TimerTask, 1000, 1000 )
	}

	@Override
	void stop( ) {
		super.stop()
		println 'FXMonitorApp.stop() called'
		context?.getBundle( 0 )?.stop()
	}

	static launchIt( ) {
		Thread.start {
			Application.launch FXMonitorApp
		}
	}

}
