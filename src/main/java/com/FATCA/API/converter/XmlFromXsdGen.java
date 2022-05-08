package com.FATCA.API.converter;

import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSParser;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlFromXsdGen {
    public static String generateXml(String xsdPath, String localPart) throws ParserConfigurationException, TransformerException {
//        creating the mockup xml file for the template
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);
        Element template = document.createElement("template");
        Element links = document.createElement("links");
        links.setTextContent(" /<!--set your links between the csv file and the xml here.." +
                "example:" +
                "/<block/>column name in csv file/</block>--/>");
        root.appendChild(template);
        root.appendChild(links);

        //here we load the xml  file stored in the storage
        final Document doc = loadXsdDocument(xsdPath);
        Element rootElem = doc.getDocumentElement();
        XSModel xsModel = new XSParser().parse(xsdPath);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        String finalString = "";
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

            finalString = result.toString();
            // here we append the template result inside the template element...
           appendXmlFragment(template, finalString);
            return XMLToString(root);
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Document loadXsdDocument(String inputName) {

        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        Document doc = null;

        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final File inputFile = new File(inputName);
            doc = builder.parse(inputFile);
        } catch (final Exception e) {
            e.printStackTrace();
            // throw new ContentLoadException(msg);
        }

        return doc;
    }

    //here we convert an xml document into a string
    public static String XMLToString(Element elt) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(elt);
        StreamResult finalResult = new StreamResult(new StringWriter());
        transformer.transform(source, finalResult);

        return finalResult.getWriter().toString();
    }

//    public static Document stringToXml(String str) throws ParserConfigurationException, IOException, SAXException {
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        System.out.println(db.parse(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))).getDocumentElement());
//        return db.parse(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
//    }

    //here we append a fragment string and insert it in parent node
    public static void appendXmlFragment (Node parent, String fragment) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = parent.getOwnerDocument();
        Node fragmentNode = docBuilder.parse(
                        new InputSource(new StringReader(fragment)))
                .getDocumentElement();
        fragmentNode = doc.importNode(fragmentNode, true);
        parent.appendChild(fragmentNode);
    }
}
