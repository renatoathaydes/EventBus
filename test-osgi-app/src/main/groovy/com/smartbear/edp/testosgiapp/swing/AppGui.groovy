package com.smartbear.edp.testosgiapp.swing

import com.smartbear.edp.api.EventManager
import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

/**
 *
 * User: Renato
 */
class AppGui {

	private static final String NO_MANAGERS = 'No manager available'
	def onClose
	private swing = new SwingBuilder()
	private hasManager = false
	EventManager manager

	private sub = new SubscriberPanel()

	private JFrame window
	private JLabel managersLabel

	AppGui( ) {
		println 'Starting AppGui'
	}

	void onBindManager( EventManager manager ) {
		this.manager = manager
		hasManager = true
		println "Added manager ${manager.name}"
		manager.subscribe sub, GuiAppEvent.class
		managersLabel?.text = manager.name
	}

	void onUnbindManager( EventManager manager ) {
		// Notice that here the service is no longer available, do not attempt to use it
		hasManager = false
		println "Removing manager"
		managersLabel?.text = NO_MANAGERS
	}

	void show( ) {
		swing.edt {
			window = frame(
					title: "Test OSGi App",
					size: [ 500, 200 ] as Dimension,
					locationRelativeTo: null,
					show: true ) {
				gridLayout( cols: 1, rows: 3 )
				sub.makeWith swing
				hbox() {
					button( "Post Event!", actionPerformed: {
						if ( hasManager ) manager.post new GuiAppEvent( new Date() )
					} )
					button( "Clear", actionPerformed: { sub.model.events = [ ] } )
				}
				vbox() {
					label( text: '**** EventManager ****'.center( 100 ) )
					managersLabel = label( text: hasManager ? manager.name : NO_MANAGERS )
				}
			}
			window.addWindowListener new AppWindowListener( onClose: onClose )
		}

	}

	void hide( ) {
		if ( window ) window.visible = false
		window = null
	}

	static void main( args ) {
		new AppGui().show()
	}

}

class AppWindowListener extends WindowAdapter {

	def onClose

	@Override
	void windowClosing( WindowEvent e ) {
		println 'Window has been closed'
		onClose?.call()
	}
}