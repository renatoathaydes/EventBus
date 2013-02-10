package com.smartbear.edp.testosgiapp.swing

import com.smartbear.edp.api.EventManager
import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.List

/**
 *
 * User: Renato
 */
class AppGui {

	private static final String NO_MANAGERS = 'No Managers'
	def onClose
	private swing = new SwingBuilder()
	List<EventManager> managers

	private sub = new SubscriberPanel()

	private JFrame window
	private JLabel managersLabel

	AppGui( ) {
		println 'Starting AppGui'
	}

	void setManager( List<EventManager> managers ) {
		this.managers = managers
		managers.each { onBindManager( it ) }
	}

	void onBindManager( EventManager manager ) {
		def managerNames = namesOf managers
		println "Added manager, managers=${managerNames}"
		manager.subscribe sub, GuiAppEvent.class
		managersLabel?.text = managerNames
	}

	void onUnbindManager( EventManager manager ) {
		// Notice that here the service is no longer available, do not attempt to use it
		def managerNames = namesOf( managers ? managers - manager : [ ] )
		println "Removing manager, remaining managers=${managerNames}"
		managersLabel?.text = managerNames
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
						managers?.each { it.post new GuiAppEvent( new Date() ) }
					} )
					button( "Clear", actionPerformed: { sub.model.events = [ ] } )
				}
				vbox() {
					label( text: '**** EventManagers ****'.center( 100 ) )
					managersLabel = label( text: namesOf( managers ) )
				}
			}
			window.addWindowListener new AppWindowListener( onClose: onClose )
		}

	}

	static namesOf( items ) {
		( !items || items.isEmpty() ) ? NO_MANAGERS : ( items*.name ).toString()
	}

	void hide( ) {
		if ( window ) window.visible = false
		window = null
	}

	static void main( args ) {
		new AppGui( ).show()
	}

}

class AppWindowListener extends WindowAdapter {

	def onClose

	@Override
	void windowClosing( WindowEvent e ) {
		println 'Window has been closed'
		if ( onClose ) onClose()
	}
}