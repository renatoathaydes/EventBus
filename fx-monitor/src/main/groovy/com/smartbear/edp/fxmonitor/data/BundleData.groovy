package com.smartbear.edp.fxmonitor.data

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.layout.HBox

/**
 *
 * User: Renato
 */
class BundleData {
	private final List<String> stateHistory = [ ]
	final sNameProp = new SimpleStringProperty()
	final HBox box

	BundleData( String sName, String state ) {
		stateHistory << state
		box = new HBox()
		def nameLabel = new Label()
		nameLabel.textProperty().bind sNameProp
		sNameProp.value = sName
		def stateLabel = new Label( state )
		box.children.addAll nameLabel, stateLabel
	}

	void update( String state ) {
		stateHistory << state
		( box.children[ 1 ] as Label ).text = state
	}

}
