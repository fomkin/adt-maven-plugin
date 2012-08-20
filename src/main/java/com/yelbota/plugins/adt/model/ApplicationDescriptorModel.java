/**
 * Copyright (C) 2012 https://github.com/yelbota/adt-maven-plugin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yelbota.plugins.adt.model;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ApplicationDescriptorModel {

    protected Document dom;

    private String versionNumber;
    private String versionLabel;
    private String content;
    private List<String> extensionIds;

    public ApplicationDescriptorModel(File source) throws MojoFailureException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(source);

        } catch (ParserConfigurationException e) {
            throwParseFail(source, e);
        } catch (SAXException e) {
            throwParseFail(source, e);
        } catch (IOException e) {
            throwParseFail(source, e);
        }
    }

    /**
     * Set properties into parsed descriptor.
     */
    public void configureDescriptor() {

        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getChildNodes();
        Element extensionsElement = null;

        for (int i = 0; i < nl.getLength(); i++) {

            Object uel = nl.item(i);

            if (uel instanceof Element) {

                Element el = (Element) uel;
                String nodeName = el.getNodeName();

                if (nodeName.equals("versionNumber")) {
                    el.setTextContent(versionNumber);
                } else if (nodeName.equals("versionLabel")) {
                    el.setTextContent(versionLabel);
                } else if (nodeName.equals("initialWindow")) {
                    NodeList initialWindowElements = el.getElementsByTagName("content");
                    initialWindowElements.item(0).setTextContent(content);
                } else if (nodeName.equals("extensions")) {
                    extensionsElement = el;
                }
            }
        }

        if (extensionsElement != null) {

            if (extensionIds == null || extensionIds.size() < 1) {
                docEle.removeChild(extensionsElement);
            } else {

                nl = extensionsElement.getChildNodes();

                // Clear <extensions>.
                while (nl.getLength() > 0)
                    extensionsElement.removeChild(nl.item(0));

                appendExtensionsElementChildren(extensionsElement);
            }

        } else {

            if (extensionIds != null && extensionIds.size() > 0) {
                extensionsElement = dom.createElement("extensions");
                appendExtensionsElementChildren(extensionsElement);
                docEle.appendChild(extensionsElement);
            }
        }
    }

    private void appendExtensionsElementChildren(Element extensionsElement) {
        for (String extensionId : extensionIds) {
            Element extensionsChildElement = dom.createElement("extensionID");
            extensionsChildElement.setTextContent(extensionId);
            extensionsElement.appendChild(extensionsChildElement);
        }

    }

    /**
     * Pretty print to outputFile
     *
     * @param outputFile file to print.
     * @throws MojoFailureException
     */
    public void printToFile(File outputFile) throws MojoFailureException {

        configureDescriptor();

        OutputFormat format = new OutputFormat(dom);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);

        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(dom);
        } catch (IOException e) {
            throw new MojoFailureException("Cant write to " + outputFile.getPath());
        }
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public String getContent() {
        return content;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public Document getDom() {
        return dom;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExtensionIds(List<String> extensionIds) {
        this.extensionIds = extensionIds;
    }

    public List<String> getExtensionIds() {
        return extensionIds;
    }

    private void throwParseFail(File source, Exception e) throws MojoFailureException {

        throw new MojoFailureException(this,
                "Cant parse application descriptor (" + source.getPath() + ")",
                e.getLocalizedMessage()
        );
    }
}
