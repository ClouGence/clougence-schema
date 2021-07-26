/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clougence.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.clougence.utils.ResourcesUtils;
import com.clougence.utils.StringUtils;
import com.clougence.config.provider.ConfigSource;
import com.clougence.config.provider.StreamType;

/**
 * 继承自{@link InputStreamSettings}父类，该类自动装载 classpath 中所有静态配置文件。
 * 并且自动装载主配置文件（该配置文件应当只有一个）。
 * @version : 2013-9-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardContextSettings extends InputStreamSettings {

    private URI settingURI;

    private void outInitLog(String mode, Object oriResource) {
        if (logger.isInfoEnabled()) {
            if (this.settingURI != null) {
                logger.info("create Settings, type = StandardContextSettings, settingsType is [{}] mode, mainSettings = {}", mode, this.settingURI);
            } else {
                if (oriResource == null) {
                    logger.info("create Settings, type = StandardContextSettings, settingsType is [{}] mode, mainSettings is not specified.", mode);
                } else {
                    logger.error("create Settings, type = StandardContextSettings, settingsType is [{}] mode, mainSettings = {}, not found.", mode, oriResource);
                }
            }
        }
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings() throws IOException, URISyntaxException{
        this("");
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(Reader mainSettings, StreamType type) throws IOException{
        if (mainSettings != null) {
            outInitLog("stream", mainSettings);
        }
        this.addConfigSource(new ConfigSource(type, mainSettings));
        refresh();
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(String mainSettings) throws IOException, URISyntaxException{
        if (StringUtils.isNotBlank(mainSettings)) {
            URL url = ResourcesUtils.getResource(mainSettings);
            if (url != null) {
                this.settingURI = url.toURI();
                outInitLog("string", mainSettings);
            }
        }
        refresh();
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final File mainSettings) throws IOException{
        if (mainSettings != null) {
            this.settingURI = mainSettings.toURI();
            outInitLog("file", mainSettings);
        }
        refresh();
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final URI mainSettings) throws IOException{
        if (mainSettings != null) {
            this.settingURI = mainSettings;
            outInitLog("uri", mainSettings);
        }
        refresh();
    }

    /**获取配置文件{@link URI}。*/
    public URI getSettingURI() { return this.settingURI; }

    @Override
    protected void readyLoad() throws IOException {
        super.readyLoad();
        //2.装载 hconfig.xml
        URI settingConfig = getSettingURI();
        if (settingConfig != null) {
            try (InputStream stream = ResourcesUtils.getResourceAsStream(settingConfig)) {
                if (stream != null) {
                    logger.info("addConfig '{}'", settingConfig);
                    _addStream(settingConfig.toURL());
                } else {
                    logger.error("not found {}", settingConfig);
                }
            }
        }
    }

    private void _addStream(URL resourceUrl) {
        String lowerCase = resourceUrl.toString().toLowerCase();
        if (lowerCase.endsWith(".xml")) {
            this.addConfigSource(new ConfigSource(StreamType.Xml, resourceUrl));
        } else if (lowerCase.endsWith(".yaml") || lowerCase.endsWith(".yml")) {
            this.addConfigSource(new ConfigSource(StreamType.Yaml, resourceUrl));
        } else {
            this.addConfigSource(new ConfigSource(StreamType.Properties, resourceUrl));
        }
    }

    @Override
    public void refresh() throws IOException {
        logger.debug("refresh -> cleanData and loadSettings...");
        this.cleanData();
        this.loadSettings();
    }
}
