package com.smartbear.edp.testosgiapp.swing

import com.smartbear.edp.api.EventManager
import com.smartbear.edp.api.EventSubscriber
import groovy.swing.SwingBuilder

import javax.swing.*

/**
 *
 * User: Renato
 */
class AppGui {

	private swing = new SwingBuilder()
	private EventManager manager


	private JFrame window

	AppGui(EventManager manager) {
		this.manager = manager
		println 'Starting AppGui'

		// it's important to explicitly declare here a dependency on javax.swing
		// and set the UIManager classLoader,
		// otherwise this bundle will not get access to Swing classes
		UIManager.getDefaults().put( "ClassLoader",
				JButton.class.classLoader )
		new JButton( )
		new JPanel( )
		new JButton( )
		new JTextArea( )
	}

	void show() {
		def sub = new SubscriberPanel()
		manager.subscribeWeakly sub, GuiAppEvent.class
		swing.edt {
			window = frame(title: "Test OSGi App", size: [500, 200], locationRelativeTo: null, show: true) {
				gridLayout(cols: 1, rows: 2)
				sub.makeWith swing
				hbox() {
					button("Post Event!", actionPerformed: { manager.post new GuiAppEvent(new Date()) })
					button("Clear", actionPerformed: { sub.model.events = [] })
				}
			}
		}
	}

	void hide() {
		if ( window ) window.visible = false
		window = null
	}

	static void main( args ) {
		new AppGui( new EventManager() {
			@Override
			def <K extends EventObject> void subscribe( EventSubscriber<K> subscriber, Class<K> eventType ) {

			}

			@Override
			def <K extends EventObject> void subscribeWeakly( EventSubscriber<K> subscriber, Class<K> eventType ) {

			}

			@Override
			def <K extends EventObject> void unSubscribe( EventSubscriber<K> subscriber ) {

			}

			@Override
			void post( EventObject event ) {

			}
		} ).show()
	}

}
