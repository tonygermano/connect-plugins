/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package io.github.tonygermano.connect.plugins.scripturlstep

import com.mirth.connect.client.ui.editors.EditorPanel
import com.mirth.connect.model.Step
import com.mirth.connect.plugins.TransformerStepPlugin

class UrlScriptStepPlugin(name: String): TransformerStepPlugin(name) {

    private val panel = UrlScriptPanel()

    override fun getPanel(): EditorPanel<Step> = panel
    
    override fun newObject(variable: String?, mapping: String): Step {
        val props = UrlScriptStep()
        props.scriptPath = mapping
        return props
    }

    override fun isNameEditable(): Boolean = true

    override fun getPluginPointName() = UrlScriptStep.PLUGIN_POINT
}