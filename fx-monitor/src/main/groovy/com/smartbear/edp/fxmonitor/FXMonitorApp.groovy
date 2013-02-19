package com.smartbear.edp.fxmonitor

import com.smartbear.edp.fxmonitor.data.BundleData
import javafx.application.Application
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleEvent
import org.osgi.framework.BundleListener

/**
 *
 * User: Renato
 */
class FXMonitorApp extends Application {

	static BundleContext context
	static bundleInfoContainer = new VBox()
	static bundlesBySName = [ : ]
	static dataInserter = [
			insert: { BundleData data ->
				bundleInfoContainer.children.setAll data.box
				dataInserter = this
			}
	]

	@Override
	public void start( Stage primaryStage ) throws Exception {
		primaryStage.title = 'OSGi Monitor Application'
		bundleInfoContainer.children.setAll new Label( 'Waiting for bundles data...' )
		Button btn = new Button()
		btn.text = "Test"
		btn.onAction = [
				handle: { ActionEvent event ->
					println "Hello World!"
				}
		] as EventHandler

		VBox root = new VBox()
		root.spacing = 20
		root.children.addAll( bundleInfoContainer, btn )
		primaryStage.scene = new Scene( root, 300, 250 )
		primaryStage.show()

	}

	static void monitorBundles( ) {
		context.addBundleListener( [
				bundleChanged: { BundleEvent bundleEvent ->
					def sName = bundleEvent.bundle.symbolicName
					def state = toStateString bundleEvent.type
					Platform.runLater {
						if ( sName in bundlesBySName ) {
							bundlesBySName[ sName ].update state
						} else {
							def data = new BundleData( sName, state )
							bundlesBySName[ sName ] = data
							dataInserter.insert data
						}
					}
				}
		] as BundleListener )
	}

	static insert( BundleData data ) {
		def children = bundleInfoContainer.children
		def insertIndex = children.findIndexOf {
			Label label -> label.text > data.sNameProp.value
		}
		if ( insertIndex < 0 ) insertIndex = children.size() - 1
		bundleInfoContainer.children.add insertIndex, data.box
	}

	static toStateString( int state ) {
		switch ( state ) {
			case BundleEvent.INSTALLED: return 'Installed'
			case BundleEvent.UNINSTALLED: return 'Uninstalled'
			case BundleEvent.RESOLVED: return 'Resolved'
			case BundleEvent.UNRESOLVED: return 'Unresolved'
			case BundleEvent.STARTING: return 'Starting'
			case BundleEvent.STARTED: return 'Started'
			case BundleEvent.STOPPED: return 'Stopped'
			case BundleEvent.STOPPING: return 'Stopping'
			case BundleEvent.UPDATED: return 'Updated'
		}
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
