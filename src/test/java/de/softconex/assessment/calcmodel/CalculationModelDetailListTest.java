package de.softconex.assessment.calcmodel;

import de.softconex.assessment.calcmodel.dto.CalculationModelDetail;
import de.softconex.assessment.calcmodel.dto.Price;
import de.softconex.assessment.calcmodel.dto.PriceRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.softconex.assessment.calcmodel.conversion.XMLConversionInterface.xmlConverter;

class CalculationModelDetailListTest extends Assertions {
    private static final Log LOG = LogFactory.getLog(CalculationModelDetailList.class);
    CalculationModelDetailList list;

    CalculationModelDetail detail0;
    CalculationModelDetail detail1;

    @Test
    final void found() {
        list = new CalculationModelDetailList();

        detail0 = new CalculationModelDetail();
        list.add(detail0);
        detail0.setPriceRange(new PriceRange(0, 19));

        detail1 = new CalculationModelDetail();
        list.add(detail1);
        detail1.setPriceRange(new PriceRange(20, 29));

        assertEquals(detail0, list.find(new Price(19)));
        assertEquals(detail1, list.find(new Price(20)));
        assertNull(list.find(new Price(30)));
    }

    @Test
    final void notFound() {
        list = new CalculationModelDetailList();

        detail0 = new CalculationModelDetail();
        list.add(detail0);
        detail0.setPriceRange(new PriceRange(0, 19));

        detail1 = new CalculationModelDetail();
        list.add(detail1);
        detail1.setPriceRange(null);

        assertEquals(detail0, list.find(new Price(19)));
        assertEquals(detail1, list.find(new Price(20)));
        assertNotNull(list.find(new Price(30)));
    }

    @Test
    void toXml() {
        list = getInitialData();
        Element writtenElement = CalculationModelDetailList.toXml(list);
        LOG.info("written: " + writtenElement.asXML());
        xmlConverter.printResults(writtenElement);

		assertNotNull(writtenElement);
		assertEquals(writtenElement.nodeCount(), list.size());

    }

    @Test
    void parse() {
        list = getInitialData();
        Element writtenElement = CalculationModelDetailList.toXml(list);
        LOG.info("written: " + writtenElement.asXML());
        xmlConverter.printResults(writtenElement);

        final CalculationModelDetailList parsed = CalculationModelDetailList.parse(writtenElement);
        assertNotNull(parsed);
        assertEquals(parsed, list);

    }

    @Test
    void sortByMinimumAscending() {
        list = getInitialData();
        list.sortByMinimumAscending();

        assertEquals(detail0, list.get(0));
        assertEquals(detail1, list.get(1));

        final CalculationModelDetail detail2 = new CalculationModelDetail(BigDecimal.ONE, new Price(BigDecimal.TEN), new PriceRange(2, 20));

        list.add(detail2);

        list.sortByMinimumAscending();
        assertEquals(detail0, list.get(0));
        assertEquals(detail2, list.get(1));
        assertEquals(detail1, list.get(2));


    }

    private CalculationModelDetailList getInitialData(){
        list = new CalculationModelDetailList();

        detail0 = new CalculationModelDetail();
        list.add(detail0);
        detail0.setPriceRange(new PriceRange(0, 19));
        detail0.setAbsolute(new Price(2));
        detail0.setPerCent(CalculationModelDetail.PERCENT_MARKUP_GREATER_199);


        detail1 = new CalculationModelDetail();
        detail1.setPriceRange(new PriceRange(3, 99));
        detail1.setAbsolute(new Price(20));
        detail1.setPerCent(CalculationModelDetail.PERCENT_MARKUP_GREATER_199);

        list.add(detail1);
      return list;
    }
}