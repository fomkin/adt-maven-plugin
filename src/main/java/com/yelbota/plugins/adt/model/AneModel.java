package com.yelbota.plugins.adt.model;

import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AneModel {

    private File file;

    private String id;

    public AneModel(File file) throws MojoFailureException {

        this.file = file;

        try {

            ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = zip.getNextEntry();
            byte[] buf = new byte[1024];

            while (entry != null) {

                if (entry.getName().endsWith("extension.xml")) {

                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    int n;
                    while ((n = zip.read(buf, 0, 1024)) > -1)
                        out.write(buf, 0, n);

                    parseXml(out.toByteArray());

                    break;
                }

                zip.closeEntry();
                entry = zip.getNextEntry();
            }

        } catch (FileNotFoundException e) {

            throw new MojoFailureException(this,
                    "Cant open ANE file " + file.getPath(),
                    e.getLocalizedMessage()
            );

        } catch (IOException e) {

            throw new MojoFailureException(this,
                    "Cant read ANE file " + file.getPath(),
                    e.getLocalizedMessage()
            );
        }

    }

    private void parseXml(byte[] s) throws MojoFailureException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(new InputSource(new ByteArrayInputStream(s)));
            NodeList nl = dom.getDocumentElement().getChildNodes();

            for (int i = 0; i < nl.getLength(); i++) {

                Object uel = nl.item(i);

                if (uel instanceof Element) {

                    Element el = (Element) uel;
                    String nodeName = el.getNodeName();

                    if (nodeName.equals("id"))
                        id = el.getTextContent();
                }
            }

        } catch (ParserConfigurationException e) {
            throwParseFail(file, e);
        } catch (SAXException e) {
            throwParseFail(file, e);
        } catch (IOException e) {
            throwParseFail(file, e);
        }
    }

    private void throwParseFail(File source, Exception e) throws MojoFailureException {

        throw new MojoFailureException(this,
                "Cant parse ANE extension.xml (" + source.getPath() + ")",
                e.getLocalizedMessage()
        );
    }

    public String getId() {
        return id;
    }
}
