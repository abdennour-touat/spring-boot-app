package com.FATCA.API.converter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlCsvGen {
    public static String generate(List<String[]> data, String templatePath) {
        try {
            File file = new File(templatePath);
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);


            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //we read the data from the csv file...
//            String fileName = csvPath;
            NodeList links = doc.getElementsByTagName("links").item(0).getChildNodes();


            ArrayList<String[]> table = new ArrayList<>(data.subList(1, data.size() - 1));

            //we get the first line which contains the columns name
//            List<String> c = Arrays.asList(r.get(0)[0].split(";"));
            ArrayList<String> columns = new ArrayList<>(Arrays.asList(data.get(0)));
            //convert every column to lowercase
            Utils.listToLowerCase(columns);
            Element temp = getTemp(doc);

            //we loop through every line and try to insert every value possible...
            for (int lineCount = 1; lineCount < table.size(); lineCount++) {
                //we get the line of data
                ArrayList<String> dataLine = new ArrayList<>(Arrays.stream(table.get(lineCount)).toList());
                //then we loop on on the links block to find matchs...
                for (int linksCount = 0; linksCount < links.getLength(); linksCount++) {
                    //if we find a valid element node we get its parent and generate
                    //a new element and try to insert the data
                    if (links.item(linksCount).getNodeType() == Node.ELEMENT_NODE) {
                        Element parent = columns.contains("type") ? (Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(Integer.parseInt(dataLine.get(0)) - 1).
                                getParentNode() : (Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(0).
                                getParentNode();
                        Element newElement = columns.contains("type") ? (Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(Integer.parseInt(dataLine.get(0)) - 1).
                                cloneNode(true) : (Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(0).
                                cloneNode(true);
                        NodeList nodeBlock = links.item(linksCount).getChildNodes();
                        //now we loop through the links blocks to find the match
                        insertBlocks(nodeBlock, columns, newElement, dataLine, doc);
                        parent.appendChild(newElement);
                    }
                }
            }
            //we transforme the element into a string to send it as an xml response..
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(temp);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);

            return result.getWriter().toString();

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException |
                 XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    //this method is to get the first child node element
    public static Element getTemp(Document doc) {
        NodeList nodes = doc.getElementsByTagName("template").item(0).getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return (Element) nodes.item(i);
            }
        }
        return null;
    }

    public static void insertBlocks(NodeList nodeList, List<String> list, Element elt, List<String> data, Document doc) throws XPathExpressionException {
        for (int itemCount = 0; itemCount < nodeList.getLength(); itemCount++) {
            //if we find a match columns we take the data line and insert all the valid data....
            if (nodeList.item(itemCount).getNodeType() == Node.ELEMENT_NODE) {
                if (list.contains(nodeList.item(itemCount).getTextContent().toLowerCase())) {
//                elt.getElementsByTagName(nodeList.item(itemCount).getNodeName()).item(0).setTextContent(data.get(list.indexOf(nodeList.item(itemCount).getTextContent().toLowerCase())));

                    display(nodeList.item(itemCount), elt, list, data, doc);
                    if (list.indexOf(nodeList.item(itemCount).getTextContent().toLowerCase()) < data.size()) {
                    }
                }
            }
        }
    }

    static void display(Node nd, Element elt, List<String> list, List<String> data, Document doc) throws XPathExpressionException {

        //we get all the element from the template
        NodeList nodeList = elt.getElementsByTagName(nd.getNodeName());
        //then we should check for every similar node whether they contain a normal content or another child nodes
        for (int item = 0; item < nodeList.getLength(); item++) {
            System.out.println("////////////");
            System.out.println("name:");
            System.out.println(nodeList.item(item).getNodeName());
            System.out.println("content:");
                System.out.println(nodeList.item(item).getTextContent());
            System.out.println("size:");
            System.out.println(nodeList.item(item).getChildNodes().getLength());
            //we get the content inside each node..

            NodeList nodeContent = nodeList.item(item).getChildNodes();
            //if the length of the content is less 1 that means that there's only text so we can change it
            //or else it  means theres other nodes and we can't make a change on it
            if (nodeContent.getLength() > 1) {
            } else {
                String info = data.get(list.indexOf(nd.getTextContent().toLowerCase()));
                Node newNode = nodeContent.item(0).cloneNode(true);
                newNode.setNodeValue(info);

                nodeList.item(item).removeChild(nodeContent.item(0));
                nodeList.item(item).appendChild(newNode);
            }
        }
    }
}