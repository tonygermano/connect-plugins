/*
 * Original work Copyright (c) Mirth Corporation. All rights reserved. http://www.mirthcorp.com
 * 
 * Modified work Copyright 2019 Tony Germano
 * Modified work Copyright 2022 Alessandro Piroddi
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 * 
 * This Source Code Form is “Incompatible With Secondary Licenses”, as
 * defined by the Mozilla Public License, v. 2.0.
 * 
 * This file is derived from the following files:
 * https://github.com/nextgenhealthcare/connect/blob/3.12.0/server/src/com/mirth/connect/connectors/http/DefaultHttpConfiguration.java
 * https://github.com/nextgenhealthcare/connect/blob/3.12.0/server/src/com/mirth/connect/server/MirthWebServer.java
 */

package io.github.tonygermano.connect.plugins.basicssl

import com.mirth.connect.client.core.PropertiesConfigurationUtil
import com.mirth.connect.connectors.http.HttpConfiguration
import com.mirth.connect.connectors.http.HttpDispatcher
import com.mirth.connect.connectors.http.HttpDispatcherProperties
import com.mirth.connect.connectors.http.HttpReceiver
import com.mirth.connect.donkey.model.channel.ConnectorPluginProperties
import com.mirth.connect.donkey.server.channel.Connector
import com.mirth.connect.server.controllers.ControllerFactory
import com.mirth.connect.server.util.ResourceUtil
import com.mirth.connect.util.MirthSSLUtil
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.ex.ConfigurationException
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.ssl.SSLContexts
import org.eclipse.jetty.http.HttpVersion
import org.eclipse.jetty.server.*
import org.eclipse.jetty.util.ssl.SslContextFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.security.KeyStore
import javax.servlet.ServletRequest

@SuppressWarnings("unused")
class BasicHttpsConfiguration : HttpConfiguration {

    private val configurationController = ControllerFactory.getFactory().createConfigurationController()

    override fun configureConnectorDeploy(connector: Connector) {
        if (connector is HttpDispatcher) {
            configureSocketFactoryRegistry(null, connector.socketFactoryRegistry)
        }
    }

    override fun configureConnectorUndeploy(connector: Connector) {}

    override fun configureReceiver(connector: HttpReceiver) {


        val mirthProperties = getMirthProperties()

        val keyStore = KeyStore.getInstance("JCEKS")
        File(mirthProperties.getString("keystore.path")).inputStream().use {
            keyStore.load(it, mirthProperties.getString("keystore.storepass").toCharArray())
        }

        val contextFactory = SslContextFactory()
        contextFactory.keyStore = keyStore
        contextFactory.certAlias = "mirthconnect"
        contextFactory.setKeyManagerPassword(mirthProperties.getString("keystore.keypass"))
        contextFactory.endpointIdentificationAlgorithm = null


        val config = org.eclipse.jetty.server.HttpConfiguration()
        config.secureScheme = "https"
        config.securePort = mirthProperties.getInt("https.port")
        config.addCustomizer(SecureRequestCustomizer())
        config.sendServerVersion = false
        config.sendXPoweredBy = false

        val sslConnector = ServerConnector(
            connector.server,
            SslConnectionFactory(contextFactory, HttpVersion.HTTP_1_1.asString()),
            HttpConnectionFactory(config)
        )

        sslConnector.name = "ssllistener"
        sslConnector.host = connector.host
        sslConnector.port = connector.port
        sslConnector.idleTimeout = connector.timeout.toLong()
        connector.server.addConnector(sslConnector)

        val lowResourceMonitor = LowResourceMonitor(connector.server)
        lowResourceMonitor.monitoredConnectors = listOf(sslConnector)
        // If the number of connections open reaches 200
        lowResourceMonitor.maxConnections = 200
        // Then close connections after 200 seconds, which is the default MaxIdleTime value. This should affect existing connections as well.
        lowResourceMonitor.lowResourcesIdleTimeout = 200000

        contextFactory.setExcludeProtocols()
        contextFactory.setExcludeCipherSuites()
        contextFactory.setIncludeProtocols(*MirthSSLUtil.getEnabledHttpsProtocols(configurationController.httpsServerProtocols))
        contextFactory.setIncludeCipherSuites(*MirthSSLUtil.getEnabledHttpsCipherSuites(configurationController.httpsCipherSuites))
    }

    override fun configureDispatcher(connector: HttpDispatcher, connectorProperties: HttpDispatcherProperties) {}

    override fun configureSocketFactoryRegistry(
        properties: ConnectorPluginProperties?,
        registry: RegistryBuilder<ConnectionSocketFactory>
    ) {
        val enabledProtocols = MirthSSLUtil.getEnabledHttpsProtocols(configurationController.httpsClientProtocols)
        val enabledCipherSuites =
            MirthSSLUtil.getEnabledHttpsCipherSuites(configurationController.httpsCipherSuites)
        val sslConnectionSocketFactory = SSLConnectionSocketFactory(
            SSLContexts.createSystemDefault(),
            enabledProtocols,
            enabledCipherSuites,
            NoopHostnameVerifier.INSTANCE
        )
        registry.register("https", sslConnectionSocketFactory)
    }

    override fun getRequestInformation(request: ServletRequest): Map<String, Any> = emptyMap<String, Any>()

    @Throws(FileNotFoundException::class, ConfigurationException::class)
    private fun getMirthProperties(): PropertiesConfiguration {
        val mirthProperties: PropertiesConfiguration
        var mirthPropsIs: InputStream? = null
        try {
            mirthPropsIs = ResourceUtil.getResourceStream(javaClass, "mirth.properties")
            mirthProperties = PropertiesConfigurationUtil.create(mirthPropsIs)
        } finally {
            ResourceUtil.closeResourceQuietly(mirthPropsIs)
        }
        return mirthProperties
    }

}