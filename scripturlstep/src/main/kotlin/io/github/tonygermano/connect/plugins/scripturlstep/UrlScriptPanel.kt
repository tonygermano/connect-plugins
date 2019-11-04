/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package io.github.tonygermano.connect.plugins.scripturlstep

import java.awt.event.ActionListener

import javax.swing.JLabel
import javax.swing.JTextField

import net.miginfocom.swing.MigLayout


import com.mirth.connect.client.ui.UIConstants
import com.mirth.connect.client.ui.editors.EditorPanel
import com.mirth.connect.model.Step

class UrlScriptPanel() : EditorPanel<Step>() {

    private var infoLabel: JLabel = JLabel("Enter the path of an external JavaScript file accessible from the Mirth Connect server.")
    private var pathLabel: JLabel = JLabel("Script Path:")
    private var pathField: JTextField = JTextField()

    init {
        setBackground(UIConstants.BACKGROUND_COLOR)
        setLayout(MigLayout("insets 8, novisualpadding, hidemode 3, gap 12 6"));

        add(infoLabel, "sx");
        add(pathLabel);
        add(pathField, "sx, growx, pushx");
    }

    override fun getDefaults(): Step = UrlScriptStep()

    override fun getProperties(): Step {
        val props = UrlScriptStep()
        props.scriptPath = pathField.text.trim()
        return props;
    }

    override fun setProperties(properties: Step?) {
        val props = properties as UrlScriptStep
        pathField.setText(props.scriptPath);
    }

    override fun checkProperties(properties: Step?, highlight: Boolean): String {
        val props = properties as UrlScriptStep
        var errors = "";

        if (props.scriptPath.isBlank()) {
            errors += "The script path cannot be blank.\n";
            if (highlight) {
                pathField.setBackground(UIConstants.INVALID_COLOR);
            }
        }

        return errors;
    }

    override fun resetInvalidProperties() {
        pathField.setBackground(null);
    }

    override fun setNameActionListener(actionListener: ActionListener?) {}
}