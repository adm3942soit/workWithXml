package de.softconex.assessment.calcmodel.dto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.softconex.assessment.calcmodel.dto.Price.*;
import static de.softconex.assessment.calcmodel.utils.ValidationUtil.*;

class PriceTest extends Assertions {
    private static final Log LOG = LogFactory.getLog(PriceTest.class);

    @Test
    final void validBigDecimalConstructor() {
        assertEquals( 1, new Price(BigDecimal.ONE).getAmount().intValue());
    }

    @Test
    final void invalidBigDecimalConstructor() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> {
                    new Price((BigDecimal) null);
                });

        assertTrue(exception.getMessage().contains(WRONG_PRICE_CONSTRUCTOR_ARGUMENT));
    }

    @Test
    final void validIntegerConstructor() {
        assertEquals(new Price(1).getAmount().intValue(), 1);
    }

    @Test
    final void invalidIntegerConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Price((Integer) null));
    }

    @Test
    final void validStringConstructor() {
        assertEquals(new Price("1").getAmount().intValue(), 1);
    }

    @Test
    final void invalidStringConstructor() {
        assertThrows(NullPointerException.class, () -> new Price((String)null));
        assertThrows(IllegalArgumentException.class, () -> new Price(NULL_STR));
    }

    @Test
    void add() {
        final Price price1 = new Price("5");

        final Price price2 = new Price("1.77");

        final Price price3 = price1.add(price2);

        assertEquals(new Price("6.77"), price3);
    }

    @Test
    void toXmlAndParseWithPositiveValue()  {
        final Price written = new Price("7.22");

        final Element writtenElement = toXml(written.getAmount().toString());
        LOG.info("writtenElement: " + writtenElement.asXML());

        final Price parsed = parse(writtenElement, PRICE_ENTITY);
        assertEquals(written, parsed);
    }

    @Test
    void toXmlAndParseWithNegativeValueThrowsException() {

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> {
                 new Price("-123.33");
            });

        assertTrue(exception.getMessage().contentEquals(WRONG_PRICE_CONSTRUCTOR_ARGUMENT));
    }

    @Test
    void parseNullThrowsException(){

        NullPointerException exception = Assertions.<NullPointerException>assertThrows(
                NullPointerException.class, () -> parse(null, PRICE_ENTITY));

    }

    @Test
    void equalsMethod() {
        assertEquals(new Price("5"), new Price("5"));
        assertNotSame(new Price("5"), new Price("15"));
        assertNotSame(new Price("5"), null);
        //assertNotSame(new Price("5"), Boolean.FALSE);

        final Price price = new Price("10");
        assertEquals(price, price);
    }

    @Test
    void hashCodeMethod() {
        assertEquals(new Price("5").hashCode(), new Price("5").hashCode());
        assertEquals(new Price("5").hashCode(), new Price("5.0").hashCode());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void staticEqualsMethod() {
        assertTrue(Price.equals(null, null));
        assertFalse(Price.equals(new Price("5"), null));
        assertFalse(Price.equals(null, new Price("5")));
        assertFalse(Price.equals(new Price("6"), new Price("5")));
        assertTrue(Price.equals(new Price("5"), new Price("5")));
        assertTrue(Price.equals(new Price("5"), new Price("5.0")));
        assertTrue(Price.equals(new Price("5.1"), new Price("5.10")));
    }
}
