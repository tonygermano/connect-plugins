/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package io.github.tonygermano.connect.plugins.scripturlstep;

import java.io.IOException
import java.net.URL

import com.mirth.connect.model.Step
import com.mirth.connect.util.ScriptBuilderException

class UrlScriptStep : Step {

    companion object { const val PLUGIN_POINT = "External Script" }

    var scriptPath = ""

    constructor() : super()

    constructor(props: UrlScriptStep) : super(props) {
        scriptPath = props.scriptPath
    }

    override fun getScript(loadFiles: Boolean) : String {
        val script = StringBuilder();
        if (loadFiles) {
            try {
                script.append("\n" + URL(scriptPath).readText() + "\n");
            } catch (e: IOException) {
                throw ScriptBuilderException("Could not add script file.", e);
            }
        } else {
            script.append("// External script will be loaded on deploy\n");
            script.append("// Path: ").append(scriptPath).append('\n');
        }
        return script.toString();
    }

    override fun getType() = PLUGIN_POINT

    override fun clone() = UrlScriptStep(this);
}
