package de.softconex.assessment.calcmodel.conversion;

import de.softconex.assessment.calcmodel.dto.Price;
import de.softconex.assessment.calcmodel.dto.PriceRange;
import org.dom4j.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.softconex.assessment.calcmodel.conversion.XMLConversion.prettyPrintByDom4j;
import static de.softconex.assessment.calcmodel.conversion.XMLConversionInterface.xmlConverter;

class XMLConversionTest extends Assertions {
    Price price;
    Element generatedPriceElement;
    PriceRange priceRange;
    Element generatedPriceRangeElement;

    @Test
    void toXml() {
        price = new Price("1");
        generatedPriceElement = xmlConverter.toXml(Price.PRICE_ENTITY, Price.PRICE_AMOUNT, price.getAmount().toString());
        xmlConverter.printResults(generatedPriceElement);
        prettyPrintByDom4j(generatedPriceElement.asXML());
        assertEquals( 1, generatedPriceElement.nodeCount());
        String amount = xmlConverter.parse(generatedPriceElement, Price.PRICE_AMOUNT);
        assertEquals( price.getAmount().toString(), amount);

        priceRange = new PriceRange(1,5);
        generatedPriceRangeElement = xmlConverter.toXml(PriceRange.PRICE_RANGE, PriceRange.PRICE_RANGE_MINIMUM, priceRange.getMinimum().toString());
        generatedPriceRangeElement = xmlConverter.addElementToRoot(generatedPriceRangeElement, PriceRange.PRICE_RANGE_MAXIMUM, priceRange.getMaximum().toString());
        prettyPrintByDom4j(generatedPriceRangeElement.asXML());
        assertEquals( 2, generatedPriceRangeElement.nodeCount());
        String min = xmlConverter.parse(generatedPriceRangeElement, PriceRange.PRICE_RANGE_MINIMUM);
        assertEquals( priceRange.getMinimum().toString(), min);
        String max = xmlConverter.parse(generatedPriceRangeElement, PriceRange.PRICE_RANGE_MAXIMUM);
        assertEquals( priceRange.getMaximum().toString(), max);

    }
}
