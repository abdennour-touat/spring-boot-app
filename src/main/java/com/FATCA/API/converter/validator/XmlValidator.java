package com.FATCA.API.converter.validator;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlValidator {
    public static List<String> validateXml(File xsdFile, File xmlFile)throws IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        XsdErrorHandler xsdValidationErrorHandler = new XsdErrorHandler();

        List<String> xsdErrors = new ArrayList<>();
        try {
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.setErrorHandler(xsdValidationErrorHandler);
            validator.validate(new StreamSource(xmlFile));

            xsdErrors.add(xsdValidationErrorHandler.getErrors());

        } catch (SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
        return xsdErrors;
    }
}
