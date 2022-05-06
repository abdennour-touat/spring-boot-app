package com.FATCA.API.converter;
import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSParser;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XmlFromXsdGen {
    public static String generateXml(String xsdPath, String localPart){
        final Document doc = loadXsdDocument(xsdPath);
        Element rootElem = doc.getDocumentElement();
        XSModel xsModel = new XSParser().parse(xsdPath);
        String result = "";
        try {
            XSInstance instance = new XSInstance();
            instance.minimumElementsGenerated = 1;
            instance.maximumElementsGenerated = 1;
            instance.generateDefaultAttributes = true;
            instance.generateOptionalAttributes = true;
//        instance.maximumRecursionDepth = 0;
//        instance.generateAllChoices = true;
            instance.showContentModel = true;
            instance.generateOptionalElements = true;
//        instance.generateFixedAttributes = true;
//        instance.maximumListItemsGenerated = 1;
            QName rootElement = new QName(rootElem.getAttribute("targetNamespace"), localPart);
            XMLDocument sampleXml = new XMLDocument(new StreamResult(result), false, 4, null);
            instance.generate(xsModel, rootElement, sampleXml);
        } catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static Document loadXsdDocument(String inputName) {
        final String filename = inputName;

        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        Document doc = null;

        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final File inputFile = new File(filename);
            doc = builder.parse(inputFile);
        } catch (final Exception e) {
            e.printStackTrace();
            // throw new ContentLoadException(msg);
        }

        return doc;
    }

}
