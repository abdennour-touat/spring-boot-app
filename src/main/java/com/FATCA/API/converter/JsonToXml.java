package com.FATCA.API.converter;
import com.github.underscore.U;

public class JsonToXml {
    public static String JsonToXml(String json){
        //this method converts json to xml
        return U.jsonToXml(json);
    }
    public static String XmlToJson(String xml){
        //this method converts xml to json
        return U.xmlToJson(xml);
    }
}
