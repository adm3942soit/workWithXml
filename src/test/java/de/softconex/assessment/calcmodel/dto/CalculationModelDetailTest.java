package de.softconex.assessment.calcmodel.dto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.softconex.assessment.calcmodel.dto.CalculationModelDetail.*;
import static de.softconex.assessment.calcmodel.dto.Price.ZERO_PRICE;

class CalculationModelDetailTest extends Assertions {
    private static final Log LOG = LogFactory.getLog(CalculationModelDetailTest.class);

    @Test
    void calculateAbsoluteOnly() {
        final CalculationModelDetail detail = new CalculationModelDetail();
        detail.setAbsolute(new Price(5));
        detail.setPerCent(null);

        Price in = new Price(1);

        Price calculated = detail.calculate(in, true );
        //markup == 10
        assertEquals(new Price(6).add(PRICE_MARKUP_0_99), calculated);


        in = new Price(100);
        calculated = detail.calculate(in, true );
        assertEquals(new Price(105).add(PRICE_MARKUP_100_199), calculated);

        in = new Price(200);
        calculated = detail.calculate(in, true );
        Price res = calculatePercentage(new Price(205), true, PERCENT_MARKUP_GREATER_199, ZERO_PRICE);
        assertEquals(res, calculated);

    }

    @Test
    void calculateAbsoluteAndPerCentNull() {
        final CalculationModelDetail detail = new CalculationModelDetail();
        detail.setAbsolute(null);
        detail.setPerCent(null);
        detail.setPriceRange(new PriceRange(null, null));

        final Price in = new Price(1);

        final Price calculated = detail.calculate(in, true);

        assertEquals(new Price(1).add(PRICE_MARKUP_0_99), calculated);
    }

    @Test
    void toStringMethod() {
        assertNotNull(new CalculationModelDetail().toString());
    }

    @Test
    void toXmlNullElements() {
        final CalculationModelDetail written = new CalculationModelDetail(null, null, null);
        try {
            final Element writtenElement = written.toXml();
            LOG.info("written: " + writtenElement.asXML());
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("is null"));
        }

    }

    @Test
    void toXmlNotNullElements() {
        final CalculationModelDetail written = new CalculationModelDetail(
                PERCENT_MARKUP_GREATER_199, new Price(5), new PriceRange(0, 99));

        final Element writtenElement = written.toXml();
        LOG.info("written: " + writtenElement.asXML());
        final CalculationModelDetail parsed = written.parse(writtenElement);
        assertNotNull(parsed);
        assertEquals(parsed, written);

    }

    @Test
    void equalsMethod() {
        assertEquals(new CalculationModelDetail(null, null, null), new CalculationModelDetail(null, null, null));
        assertEquals(new CalculationModelDetail(BigDecimal.ONE, new Price(2), new PriceRange(1, 5)),
                new CalculationModelDetail(BigDecimal.ONE, new Price(2), new PriceRange(1, 5)));
        assertNotSame(new CalculationModelDetail(BigDecimal.ONE, new Price(BigDecimal.TEN), new PriceRange(0, 5)),
                new CalculationModelDetail(BigDecimal.ONE, new Price(BigDecimal.TEN), new PriceRange(1, 5)));
        assertNotSame(new CalculationModelDetail(BigDecimal.ONE, new Price("3"), new PriceRange(1, 4)),
                new CalculationModelDetail(BigDecimal.ONE, new Price("3"), new PriceRange(1, 5)));
        assertNotSame(new CalculationModelDetail(BigDecimal.ONE, new Price("3"), new PriceRange(1, 4)),
                new CalculationModelDetail(BigDecimal.TEN, new Price("3"), new PriceRange(1, 4)));
        assertNotSame(new CalculationModelDetail(BigDecimal.ONE, new Price("3"), new PriceRange(1, 4)),
                new CalculationModelDetail(BigDecimal.ONE, new Price("2"), new PriceRange(1, 4)));
        assertNotSame(new CalculationModelDetail(BigDecimal.ONE, new Price("3"), new PriceRange(1, 4)), new PriceRange(1, 4));

        final CalculationModelDetail calculationModelDetail = new CalculationModelDetail(BigDecimal.ONE, new Price("3"), new PriceRange(1, 4));
        assertEquals(calculationModelDetail, calculationModelDetail);
    }

}
