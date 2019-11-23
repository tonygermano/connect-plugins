/*
 * Copyright Tony Germano
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 */

 package io.github.tonygermano.connect.plugins.basichttpsconfig;

import com.mirth.connect.model.ExtensionPermission;
import com.mirth.connect.plugins.ServicePlugin;
import com.mirth.connect.server.controllers.ControllerFactory;
import java.util.Properties;

/**
 * This plugin registers BasicHttpsConfiguration so that the HTTP Listener will pick it up
 * 
 * @author Tony Germano
 */
class BasicHttpsPlugin : ServicePlugin {

    private val configurationController = ControllerFactory.getFactory().createConfigurationController();

    override fun getPluginPointName() = "BasicHttpsConfiguration";

    override fun start() {}

    override fun stop() {}

    override fun init(properties: Properties) {
        configurationController.saveProperty("HTTP", "httpConfigurationClass",
            "io.github.tonygermano.connect.plugins.basichttpsconfig.BasicHttpsConfiguration");
    }

    override fun update(properties: Properties) {}

    override fun getDefaultProperties(): Properties = Properties();

    override fun getExtensionPermissions(): Array<ExtensionPermission>? = null;
}