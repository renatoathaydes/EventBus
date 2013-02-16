package com.smartbear.edp.testosgiapp.swing

import com.smartbear.edp.api.EventManager
import com.smartbear.edp.api.EventSubscriber

/**
 *
 * User: Renato
 */
class AppGuiTest extends GroovyTestCase {


	void testGui( ) {
		def log = [ ]
		def subs = [ ]
		def manager = [
				getName: { 'Test Manager' },
				subscribe: { sub, clazz ->
					log << 'Subscribing to ' + clazz
					subs << sub
				},
				subscribeWeakly: { sub, clazz -> log << 'Subscribing weakly to ' + clazz },
				unSubscribe: { EventSubscriber sub -> log << 'Unsubscribing' },
				post: { EventObject event ->
					log << 'Posting event'
					subs.each{ it.handle event }
				}
		] as EventManager

		def gui = new AppGui( )
		gui.sub  = [
				handle: { GuiAppEvent event -> log << 'Handling event' }
		] as EventSubscriber

		gui.onBindManager( manager )
		manager.post new GuiAppEvent( 'hello' )

		assert log == [
				'Subscribing to ' + GuiAppEvent.class,
				'Posting event',
				'Handling event'
		]

	}

}
