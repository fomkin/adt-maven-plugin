package com.yelbota.plugins.adt.utils;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class ApplicationDescriptorConfigurator {

    protected Document dom;

    private String versionNumber;
    private String versionLabel;
    private String content;

    public ApplicationDescriptorConfigurator(File source) throws MojoFailureException {

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
                }
            }
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

    private void throwParseFail(File source, Exception e) throws MojoFailureException {

        throw new MojoFailureException(this,
                "Cant parse application descriptor (" + source.getPath() + ")",
                e.getLocalizedMessage()
        );
    }

}
