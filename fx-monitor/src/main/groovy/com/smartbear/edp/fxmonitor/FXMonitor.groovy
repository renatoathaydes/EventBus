package com.smartbear.edp.fxmonitor

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

/**
 *
 * User: Renato
 */
class FXMonitor {

	void start( ) {
		println 'Launching FXMonitorApp!'
		FxMonitorApp.launch( )
//		println "Starting FXMonitor with GroovyFX class: ${GroovyFX.class.name}"
//		GroovyFX.start {
//			stage( title: 'GroovyFX Hello World', visible: true ) {
//				scene( fill: BLACK, width: 500, height: 250 ) {
//					hbox( padding: 60 ) {
//						text( text: 'Groovy', font: '80pt sanserif' ) {
//							fill linearGradient( endX: 0, stops: [ PALEGREEN, SEAGREEN ] )
//						}
//						text( text: 'FX', font: '80pt sanserif' ) {
//							fill linearGradient( endX: 0, stops: [ CYAN, DODGERBLUE ] )
//							effect dropShadow( color: DODGERBLUE, radius: 25, spread: 0.25 )
//						}
//					}
//				}
//			}
//		}

	}

	static void main( args ) {
		new FXMonitor().start()
	}


}

class FxMonitorApp extends Application {

	@Override
	void start( Stage primaryStage ) throws Exception {
		primaryStage.setTitle( "Hello World!" );
		Button btn = new Button();
		btn.setText( "Say 'Hello World'" );
		btn.setOnAction( new EventHandler<ActionEvent>() {

			@Override
			public void handle( ActionEvent event ) {
				System.out.println( "Hello World!" );
			}
		} );

		StackPane root = new StackPane();
		root.getChildren().add( btn );
		primaryStage.setScene( new Scene( root, 300, 250 ) );
		primaryStage.show();
	}

}
