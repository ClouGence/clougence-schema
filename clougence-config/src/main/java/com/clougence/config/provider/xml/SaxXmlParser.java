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
package com.clougence.config.provider.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.clougence.utils.StringUtils;
import com.clougence.config.SettingNode;
import com.clougence.config.Settings;
import com.clougence.config.data.TreeNode;

/**
 * @version : 2013-7-13
 * @author 赵永春 (zyc@byshell.org)
 */
class SaxXmlParser extends DefaultHandler {

    private final Map<String, TreeNode>        root    = new HashMap<>();
    private final Map<String, Stack<TreeNode>> curNode = new HashMap<>();
    private String                             curXmlns;
    private final Settings                     settings;

    public SaxXmlParser(Settings settings){
        this.settings = settings;
    }

    private TreeNode getRoot(String uri) {
        TreeNode treeNode = this.root.get(uri);
        if (treeNode == null) {
            treeNode = new TreeNode("", uri);
            this.root.put(uri, treeNode);
        }
        return treeNode;
    }

    private TreeNode curNode(String uri) {
        Stack<TreeNode> stack = this.curNode.computeIfAbsent(uri, k -> new Stack<>());
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    private TreeNode pushNode(String uri, TreeNode treeNode) {
        Stack<TreeNode> stack = this.curNode.computeIfAbsent(uri, k -> new Stack<>());
        stack.push(treeNode);
        return treeNode;
    }

    private TreeNode popNode(String uri) {
        Stack<TreeNode> stack = this.curNode.computeIfAbsent(uri, k -> new Stack<>());
        return stack.pop();
    }

    private TreeNode newNode(String uri, String localName) {
        TreeNode curNode = this.curNode(uri);
        if (curNode == null) {
            return this.pushNode(uri, this.getRoot(uri));
        } else {
            TreeNode newSubNode = curNode.newLast(localName.trim());
            return pushNode(uri, newSubNode);
        }
    }

    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
        TreeNode treeNode = this.newNode(uri, localName);
        for (int i = 0; i < attributes.getLength(); i++) {
            String attName = attributes.getLocalName(i);
            String attValue = attributes.getValue(i);
            if (StringUtils.isBlank(attName)) {
                continue;
            }
            treeNode.addValue(attName.trim(), attValue);
        }
        this.curXmlns = uri;
    }

    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.curXmlns == null) {
            return;
        }
        String dat = new String(ch, start, length);
        if (StringUtils.isNotBlank(dat)) {
            curNode(this.curXmlns).addValue(dat.trim());
        }
    }

    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.popNode(uri);
        this.curXmlns = uri;
    }

    public void endDocument() {
        this.root.forEach((xmlns, treeNode) -> {
            for (SettingNode node : treeNode.getSubNodes()) {
                this.settings.addSetting(node.getName(), node, xmlns);
            }
        });
    }
}
