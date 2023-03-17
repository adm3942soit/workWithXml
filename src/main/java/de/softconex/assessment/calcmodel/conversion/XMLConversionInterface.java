package de.softconex.assessment.calcmodel.conversion;

import org.dom4j.Document;
import org.dom4j.Element;

import javax.validation.constraints.NotNull;

public interface XMLConversionInterface {
    XMLConversionInterface xmlConverter = new XMLConversion();

    Element createRootElement(@NotNull Document document, @NotNull String xmlElementName);

    Element toXml(@NotNull String xmlElementName, @NotNull String xmlPropertyName, @NotNull String xmlPropertyValue);


    Element addElementToRoot(@NotNull Element root, @NotNull String xmlPropertyName, @NotNull String xmlPropertyValue);


    String parse(@NotNull Element parent, @NotNull String xmlPropertyName);

    Element parseElement(@NotNull Element element, @NotNull String xmlElementName);

    void printResults(@NotNull Element rootElement);

}
