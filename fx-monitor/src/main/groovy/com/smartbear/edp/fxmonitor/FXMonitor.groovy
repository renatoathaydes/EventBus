package com.smartbear.edp.fxmonitor

import javafx.application.Application
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.WindowEvent

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

	static void main( args ) {
		new FXMonitor().start()
	}

}

public class FXMonitorApp extends Application {

	@Override
	public void start( Stage primaryStage ) throws Exception {
		primaryStage.title = 'OSGi Monitor Application'
		Button btn = new Button();
		btn.text = "Test"
		btn.onAction = [
			handle: { ActionEvent event ->
				println "Hello World!"
			}
		] as EventHandler

		StackPane root = new StackPane();
		root.children.add( btn );
		primaryStage.scene = new Scene( root, 300, 250 )
		primaryStage.show();
	}

	@Override
	void stop( ) {
		super.stop()
		println 'FXMonitorApp.stop() called'
		//TODO stop the bundle
	}

	static launchIt( ) {
		Thread.start {
			Application.launch( FXMonitorApp )
		}
	}

}
