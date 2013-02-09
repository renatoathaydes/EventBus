package com.smartbear.edp.testosgiapp.swing

import com.smartbear.edp.api.EventSubscriberBase
import groovy.beans.Bindable

/**
 *
 * User: Renato
 */
class SubscriberPanel extends EventSubscriberBase<GuiAppEvent> {

	class Model { @Bindable def events = [] }

	final model = new Model()
	final MAX_EVENTS = 5

	def makeWith( swing ) {
		swing.panel() {
			textArea(editable: false, lineWrap: true, preferredSize: [400, 70],
					text: bind(source: model, sourceProperty: "events",
					converter: { list ->  list ?
						"Latest events captured: $list" :
						'No events captured'}))
		}
	}

	@Override
	void handle( GuiAppEvent event ) {
		println "Handling event $event"
		model.events += event
		if ( model.events.size() > MAX_EVENTS )
			model.events = model.events.tail()
	}

}

class GuiAppEvent extends EventObject {

	GuiAppEvent( Object source ) { super( source ) }

	String toString() { source.toString() }

}
