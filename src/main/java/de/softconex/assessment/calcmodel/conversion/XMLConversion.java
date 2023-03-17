package de.softconex.assessment.calcmodel.conversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;


public class XMLConversion implements XMLConversionInterface {
    private static final Log logger = LogFactory.getLog(XMLConversion.class);

    @NotNull
    /*create xml by object property*/
    public Element toXml(@NotNull String xmlElementName, @NotNull String xmlPropertyName, @NotNull String xmlPropertyValue) {
        Document document = DocumentHelper.createDocument();
        Element root = createRootElement(document, xmlElementName);
        return addElementToRoot(root, xmlPropertyName, xmlPropertyValue);
    }

    @NotNull
    public Element createRootElement(@NotNull Document document, @NotNull String xmlElementName) {
        return document.addElement(xmlElementName);
    }

    @NotNull
    public Element addElementToRoot(@NotNull Element root, @NotNull String xmlPropertyName, @NotNull String xmlPropertyValue) {
        Element child = root.addElement(xmlPropertyName, xmlPropertyValue);
        child.add(DocumentHelper.createAttribute(child, xmlPropertyName, xmlPropertyValue));
        root.add(DocumentHelper.createAttribute(root, xmlPropertyName, xmlPropertyValue));
        return root;
    }

    public String parse(@NotNull Element element, @NotNull String xmlPropertyName) {
        try {
            Iterator<?> it = element.nodeIterator();
            while (it.hasNext()) {
                Element current = (Element) it.next();
                for (Attribute attribute : current.attributes()) {
                    if (attribute.getName().equals(xmlPropertyName)) {
                        return attribute.getValue();
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("element = " + element + ", xmlPropertyName = "
                    + xmlPropertyName + ": " + exception.getMessage());
        }
        return null;
    }
    public Element parseElement(@NotNull Element element, @NotNull String xmlElementName) {
        try {
            Iterator<?> it = element.nodeIterator();
            while (it.hasNext()) {
                Element current = (Element) it.next();
                if(current.getName().equals(xmlElementName)){
                    return current;
                }
            }
        } catch (Exception exception) {
            logger.error("element = " + element + ", xmlElementName = "
                    + xmlElementName + ": " + exception.getMessage());
        }
        return null;
    }

    public void printResults(@NotNull Element rootElement) {
        final OutputFormat format = getFormat();
        XMLWriter writer;
        try {
            StringWriter stringWriter = new StringWriter();
            writer = new XMLWriter(stringWriter, format);
            writer.write(rootElement);
            writer.flush();
            writer.close();
            System.out.println(stringWriter);
        } catch (IOException ex) {
            logger.error("Print document: " + ex.getMessage());
        }
    }

    public static void prettyPrintByDom4j(@NotNull String xmlString) {
        try {
            final OutputFormat format = getFormat();

            org.dom4j.Document document = DocumentHelper.parseText(xmlString);
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
            writer.flush();
            writer.close();
            logger.info(sw);
            sw.close();
        } catch (Exception e) {
            String message = "Error occurs when pretty-printing xml:" + e.getMessage();
            logger.error(message);
        }
    }

    @NotNull
    private static OutputFormat getFormat() {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(5);
        format.setSuppressDeclaration(true);
        format.setEncoding("UTF-8");
        format.setNewlines(true);
        return format;
    }
}