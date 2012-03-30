package com.yelbota.plugins.adt.model;

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDescriptorModelTest {

    private final File wd = new File(".");

    @Test
    public void configureDescriptorTest() {

        try {

            File f = FileUtils.resolveFile(wd, "src/test/resources/unit/application-descriptor.xml");
            ApplicationDescriptorModel c = new ApplicationDescriptorModel(f);

            List<String> extensionIds = new ArrayList<String>();

            extensionIds.add("com.example.Ext1");
            extensionIds.add("ext2");

            c.setVersionLabel("1.0-SNAPSHOT");
            c.setVersionNumber("1.0.0");
            c.setContent("application-1.0-SNAPSHOT.swf");
            c.setExtensionIds(extensionIds);

            c.configureDescriptor();

            testDom(c.getDom(), c);

        } catch (MojoFailureException e) {
            Assert.fail(e.toString());
        } catch (XPathExpressionException e) {
            Assert.fail("Fail during test execution " + e);
        }
    }

    @Test
    public void configureDescriptorWithExtensionsTagTest() {

        try {

            File f = FileUtils.resolveFile(wd, "src/test/resources/unit/application-descriptor-with-extensions-tag.xml");
            ApplicationDescriptorModel c = new ApplicationDescriptorModel(f);

            List<String> extensionIds = new ArrayList<String>();

            extensionIds.add("com.example.Ext1");
            extensionIds.add("ext2");

            c.setExtensionIds(extensionIds);
            c.configureDescriptor();

            testDomExtensions(c.getDom(), c);

        } catch (MojoFailureException e) {
            Assert.fail(e.toString());
        } catch (XPathExpressionException e) {
            Assert.fail("Fail during test execution " + e);
        }
    }

    @Test
    public void printToFileTest() {

        try {

            File f = FileUtils.resolveFile(wd, "src/test/resources/unit/application-descriptor.xml");
            ApplicationDescriptorModel c = new ApplicationDescriptorModel(f);

            c.setVersionLabel("1.0-SNAPSHOT");
            c.setVersionNumber("1.0.0");
            c.setContent("application-1.0-SNAPSHOT.swf");

            f = FileUtils.resolveFile(wd, "target/unit/application-descriptor.xml");
            f.getParentFile().mkdirs();
            c.printToFile(f);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(f);

            testDom(dom, c);

        } catch (MojoFailureException e) {
            Assert.fail(e.toString());
        } catch (XPathExpressionException e) {
            Assert.fail("Fail during test execution " + e);
        } catch (ParserConfigurationException e) {
            Assert.fail("Fail during test execution " + e);
        } catch (SAXException e) {
            Assert.fail("Fail during test execution " + e);
        } catch (IOException e) {
            Assert.fail("Fail during test execution " + e);
        }
    }

    private void testDom(Document doc, ApplicationDescriptorModel c) throws XPathExpressionException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        Assert.assertEquals(
                xpath.compile("//versionLabel/text()").evaluate(doc),
                c.getVersionLabel(),
                "versionLabel should be " + c.getVersionLabel()
        );

        Assert.assertEquals(
                xpath.compile("//versionNumber/text()").evaluate(doc),
                c.getVersionNumber(),
                "versionNumber should be " + c.getVersionNumber()
        );

        Assert.assertEquals(
                xpath.compile("//initialWindow/content/text()").evaluate(doc),
                c.getContent(),
                "content should be " + c.getContent()
        );

        testDomExtensions(doc, c);
    }

    private void testDomExtensions(Document doc, ApplicationDescriptorModel c) throws XPathExpressionException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        Object result = xpath.compile("//extensions/extensionID/text()").evaluate(doc, XPathConstants.NODESET);

        if (result instanceof NodeList) {

            NodeList nodes = (NodeList) result;

            if (c.getExtensionIds() == null || c.getExtensionIds().size() < 1) {

                Assert.assertTrue(nodes.getLength() == 0);
            }
            else {

                List<String> modelExtensions = c.getExtensionIds();
                List<Text> filteredNodes = new ArrayList<Text>();

                for (int i = 0; i < nodes.getLength(); i++) {

                    Object node = nodes.item(i);

                    if (node instanceof Text)
                        filteredNodes.add((Text) node);
                }

                if (modelExtensions.size() != filteredNodes.size()) {

                    Assert.fail();
                } else {

                    for (int i = 0; i < filteredNodes.size(); i++) {
                        String nodeContent = filteredNodes.get(i).getTextContent();
                        String modelExtensionId = modelExtensions.get(i);
                        Assert.assertEquals(nodeContent, modelExtensionId);
                    }
                }
            }
        } else {
            Assert.fail("Extensions is not node list");
        }
    }

}
