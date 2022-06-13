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
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlCsvGen {
    public static String generate(List<String[]> data, String templatePath) {
        try {
            //build the document from the template path..
            Document doc = buildDocument(templatePath);
            NodeList links = filterNodes((Element) doc.getElementsByTagName("links").item(0).getChildNodes());
            Element allLinks = (Element) links;
            ArrayList<String[]> table = new ArrayList<>(data.subList(0, data.size()));

            //we get the first line containing the columns names
            ArrayList<String> columns = new ArrayList<>(Arrays.asList(data.get(0)));
            //set every column to lowercase
            Utils.listToLowerCase(columns);
            Element temp = getTemp(doc);

            //we loop through every line and try to insert every value possible...
            for (int lineCount = 1; lineCount < table.size(); lineCount++) {
                //we get the line of data
                ArrayList<String> dataLine = new ArrayList<>(Arrays.stream(table.get(lineCount)).toList());
                //get the first value that contains the type..
                int type = columns.contains("type") ? Integer.parseInt(dataLine.get(0)) - 1 : 0;
                //we get the corresponding iterative tag with the type
                if(allLinks.getElementsByTagName(links.item(0).getNodeName()).getLength() < Integer.parseInt(dataLine.get(0))){
                    type = 0;
                }
                Node selectedNode = allLinks.getElementsByTagName(links.item(0).getNodeName()).item(type);
                //a new element and try to insert the data
                Element parent = (Element) doc.
                        getElementsByTagName(selectedNode.getNodeName()).
                        item(type).
                        getParentNode();
                Element newElement = (Element) doc.
                        getElementsByTagName(selectedNode.getNodeName()).
                        item(type).
                        cloneNode(true);
                //we get the child nodes that have the links with the csv file
                NodeList nodeBlock = selectedNode.getChildNodes();
                //now we loop through the links blocks to find the match
                insertBlocks(nodeBlock, columns, newElement, dataLine);
                parent.appendChild(newElement);
            }
            //delete the sample nodes..
            for (int x = 0; x < allLinks.getElementsByTagName(links.item(0).getNodeName()).getLength(); x ++){
               Node nd = doc.getElementsByTagName(links.item(0).getNodeName()).item(0);
               nd.getParentNode().removeChild(nd);
            }

            return transformToString(temp);

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
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

    public static void insertBlocks(NodeList nodeList, List<String> list, Element elt, List<String> data) {
        for (int itemCount = 0; itemCount < nodeList.getLength(); itemCount++) {

            //if we find a match columns we take the data line and insert all the valid data....
            if (nodeList.item(itemCount).getNodeType() == Node.ELEMENT_NODE) {
                if (nodeList.item(itemCount).getChildNodes().getLength() == 1) {
                    if (list.contains(nodeList.item(itemCount).getTextContent().toLowerCase())) {
                        insert(nodeList.item(itemCount), elt, list, data);
                    }
                } else if (nodeList.item(itemCount).getChildNodes().getLength() > 1) {
                    NodeList indexedNodes = nodeList.item(itemCount).getChildNodes();
                    for (int indexedCounter = 0; indexedCounter < indexedNodes.getLength(); indexedCounter++) {

                        if (list.contains(indexedNodes.item(indexedCounter).getTextContent().toLowerCase())) {
                            insertIndexed(indexedNodes.item(indexedCounter), elt, list, data);

                        }
                    }
                }

            }
        }
    }

    static void insertIndexed(Node nd, Element elt, List<String> list, List<String> data) {

        //we get all the element from the template
        Node parent = nd.getParentNode();

        NodeList nodeList = elt.getElementsByTagName(nd.getNodeName());
        //then we should check for every similar node whether they contain a normal content or another child nodes
        for (int item = 0; item < nodeList.getLength(); item++) {

//            we get the content inside each node..

            NodeList nodeContent = nodeList.item(item).getChildNodes();
            //if the length of the content is less 1 that means that there's only text so we can change it
            //or else it  means theres other nodes and we can't make a change on it
            if (nodeContent.getLength() <= 1) {
                // we add one more check to verify if the target tag is a child of the correct parent tag
//                isContained(parent.getChildNodes(), nodeList.item(item).getNodeName());
                if (nodeList.item(item).getParentNode().getNodeName().equals(parent.getNodeName())) {
//                if (isContained(parent.getChildNodes(), nodeList.item(item).getNodeName())) {
                    String info = data.get(list.indexOf(nd.getTextContent().toLowerCase()));
                    Node newNode = nodeContent.item(0).cloneNode(true);
                    newNode.setNodeValue(info);

                    nodeList.item(item).removeChild(nodeContent.item(0));
                    nodeList.item(item).appendChild(newNode);
                }
            }
//            }  //


        }
    }

    static void insert(Node nd, Element elt, List<String> list, List<String> data) {

        //we get all the element from the template
        NodeList nodeList = elt.getElementsByTagName(nd.getNodeName());
        //then we should check for every similar node whether they contain a normal content or another child nodes
        for (int item = 0; item < nodeList.getLength(); item++) {

            //we get the content inside each node..

            NodeList nodeContent = nodeList.item(item).getChildNodes();
            //if the length of the content is less than 1 that means that there's only text so we can change it
            //or else it  means theres other nodes and we can't make a change on it
            if (nodeContent.getLength() <= 1) {

                String info = data.get(list.indexOf(nd.getTextContent().toLowerCase()));
                Node newNode = nodeContent.item(0).cloneNode(true);
                newNode.setNodeValue(info);

                nodeList.item(item).removeChild(nodeContent.item(0));
                nodeList.item(item).appendChild(newNode);
            }  //
        }
    }

    private static String transformToString(Element temp) throws TransformerException {
        //we transforme the element into a string to send it as an xml response..
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(temp);
        StreamResult result = new StreamResult(new StringWriter());
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    private static Document buildDocument(String templatePath) throws ParserConfigurationException, SAXException, IOException {
        File file = new File(templatePath);
        //an instance of factory that gives a document builder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    static boolean isContained(NodeList nodeList, String nodeName) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeName().equals(nodeName)) {
                return true;
            } else {
                NodeList children = childNode.getChildNodes();
                isContained(children, nodeName);
            }
        }
        return false;
    }

    static NodeList filterNodes(Element elt) {
        Element newElt = (Element) elt.cloneNode(false);
        NodeList list = elt.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);

            if (item.getNodeType() == Node.ELEMENT_NODE){
                newElt.appendChild(item);
            }
        }
        return newElt.getChildNodes();
    }
}

