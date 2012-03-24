package com.yelbota.plugins.adt.utils;

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

public class ApplicationDescriptorConfiguratorTest {

    private final File wd = new File(".");

    @Test
    public void configureDescriptorTest() {

        try {

            File f = FileUtils.resolveFile(wd, "src/test/resources/unit/application-descriptor.xml");
            ApplicationDescriptorConfigurator c = new ApplicationDescriptorConfigurator(f);

            c.setVersionLabel("1.0-SNAPSHOT");
            c.setVersionNumber("1.0.0");
            c.setContent("application-1.0-SNAPSHOT.swf");

            c.configureDescriptor();

            testDom(c.getDom(), c);

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
            ApplicationDescriptorConfigurator c = new ApplicationDescriptorConfigurator(f);

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

    private void testDom(Document doc, ApplicationDescriptorConfigurator c) throws XPathExpressionException {

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

    }
}
