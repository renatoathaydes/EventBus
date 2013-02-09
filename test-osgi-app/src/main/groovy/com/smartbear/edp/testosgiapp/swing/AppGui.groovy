package com.smartbear.edp.testosgiapp.swing

import com.smartbear.edp.api.EventManager
import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.Dimension

/**
 *
 * User: Renato
 */
class AppGui {

	private swing = new SwingBuilder()
	private List<EventManager> managers


	private JFrame window

	AppGui( List<EventManager> managers ) {
		this.managers = managers
		println 'Starting AppGui'
	}

	void show( ) {
		def sub = new SubscriberPanel()
		//FIXME if service changes, we have to re-subscribe
		managers.each { it.subscribeWeakly sub, GuiAppEvent.class }
		swing.edt {
			window = frame(
					title: "Test OSGi App",
					size: [ 500, 200 ] as Dimension,
					locationRelativeTo: null,
					defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
					show: true ) {
				gridLayout( cols: 1, rows: 2 )
				sub.makeWith swing
				hbox() {
					button( "Post Event!", actionPerformed: {
						managers.each { it.post new GuiAppEvent( new Date() ) }
					} )
					button( "Clear", actionPerformed: { sub.model.events = [ ] } )
				}
			}
		}
	}

	void hide( ) {
		if ( window ) window.visible = false
		window = null
	}

	static void main( args ) {
		new AppGui( null ).show()
	}

}
