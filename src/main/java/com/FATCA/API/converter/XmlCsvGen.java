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
    public static String generate(List<String[]> data, String templatePath){
        try{
            File file = new File(templatePath);
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //we read the data from the csv file...
//            String fileName = csvPath;
            NodeList links = doc.getElementsByTagName("links").item(0).getChildNodes();
//
//            CSVReader reader = new CSVReader(new FileReader(fileName));
//            List<String[]> r = reader.readAll();

            System.out.println(data);
            ArrayList<String[]> table = new ArrayList<>(data.subList(1, data.size()-1));
//            for (String[] str:table){
//                System.out.println(Arrays.toString(str));
//            }
//            ArrayList<String[]> table = (ArrayList<String[]>) data;
            //we get the first line which contains the columns name
//            List<String> c = Arrays.asList(r.get(0)[0].split(";"));
            ArrayList<String> columns = new ArrayList<>(Arrays.asList(data.get(0)));
            //convert every column to lowercase
            Utils.listToLowerCase(columns);
            Element temp = getTemp(doc);

            //we loop through every line and try to insert every value possible...
            for (int lineCount = 1 ; lineCount< table.size(); lineCount++) {
                //we get the line of data
                System.out.println(Arrays.toString(table.get(lineCount)));
                ArrayList<String> dataLine = new ArrayList<>(Arrays.stream(table.get(lineCount)).toList());
                //then we loop on on the links block to find matchs...
                for (int linksCount = 0; linksCount < links.getLength(); linksCount++) {
                    //if we find a valid element node we get its parent and generate
                    //a new element and try to insert the data
                    if (links.item(linksCount).getNodeType() == Node.ELEMENT_NODE) {
//                        System.out.println(links.item(linksCount));
                        Element parent = dataLine.contains("type")? (Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(Integer.parseInt(dataLine.get(0))-1).
                                getParentNode():(Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(0).
                                getParentNode();
//                        System.out.println(parent);
                        Element newElement = dataLine.contains("type")?(Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(Integer.parseInt(dataLine.get(0))-1).
                                cloneNode(true):(Element) doc.
                                getElementsByTagName(links.item(linksCount).getNodeName()).
                                item(0).
                                cloneNode(true);

//                        System.out.println(newElement);
                        NodeList nodeBlock = links.item(linksCount).getChildNodes();
                        //now we loop through the links blocks to find the match
//                        System.out.println("data line"+dataLine);
//                        System.out.println("columns"+ columns);
                        insertBlocks(nodeBlock, columns, newElement, dataLine);
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
//            System.out.println(result.getWriter().toString());

            return result.getWriter().toString();

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    //this method is to get the first child node element
    public static Element getTemp(Document doc){
        NodeList nodes = doc.getElementsByTagName("template").item(0).getChildNodes();
        for (int i = 0; i< nodes.getLength(); i++){
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                return (Element) nodes.item(i);
            }
        }
        return null;
    }
    public static void insertBlocks(NodeList nodeList, List<String> list, Element elt, List<String> data){
        for (int itemCount = 0; itemCount < nodeList.getLength(); itemCount++) {

            //if we find a match columns we take the data line and insert all the valid data....
            if (list.contains(nodeList.item(itemCount).getTextContent().toLowerCase())) {
                if (list.indexOf(nodeList.item(itemCount).getTextContent().toLowerCase()) < data.size()) {
                    elt.getElementsByTagName(nodeList.item(itemCount).getNodeName()).item(0).setTextContent(data.get(list.indexOf(nodeList.item(itemCount).getTextContent().toLowerCase())));
                }
            }
        }
    }
}
