package de.softconex.assessment.calcmodel.dto;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static de.softconex.assessment.calcmodel.conversion.XMLConversionInterface.xmlConverter;
import static de.softconex.assessment.calcmodel.dto.Price.*;
import static de.softconex.assessment.calcmodel.utils.ValidationUtil.*;

/**
 * {@link Price} range (min/max).
 */
public class PriceRange {
    public static final String PRICE_RANGE = "priceRange";
    public static final String PRICE_RANGE_MINIMUM = "minimum";
    public static final String PRICE_RANGE_MAXIMUM = "maximum";
    public static final PriceRange PRICE_RANGE_0_99 = new PriceRange(ZERO_PRICE, PRICE_99);
    public static final PriceRange PRICE_RANGE_100_199 = new PriceRange(PRICE_100, PRICE_199);
    public static final PriceRange PRICE_RANGE_GREATER_199 = new PriceRange(PRICE_199, null);
    private final Price minimum;

    private final Price maximum;

    public PriceRange() {
        this.minimum = new Price();
        this.maximum = new Price();
    }

    public PriceRange(int minimum, int maximum) {
        this.minimum = new Price(minimum);
        this.maximum = new Price(maximum);
    }

    public PriceRange(Price minimum, Price maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public final Price getMaximum() {
        return this.maximum;
    }

    public final Price getMinimum() {
        return this.minimum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceRange)) return false;
        PriceRange that = (PriceRange) o;
        return Objects.equals(getMinimum(), that.getMinimum()) && Objects.equals(getMaximum(), that.getMaximum());
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum);
    }

    @Override
    public String toString() {
        return "Price Range: "
                + (getMinimum() == null ? NULL_STR : getMinimum().toString()) + "-"
                + (getMaximum() == null ? NULL_STR : getMaximum().toString());
    }

    /**
     * Returns whether the passed price is inside "this" range. Null values
     * minimum/maximum will be ignored (considered as "no limit").
     */
    public boolean contains(@NotNull Price price) {
        if (getMinimum() != null && (getMinimum().getAmount().compareTo(price.getAmount()) > 0)) {
            return false;
        }

        if (getMaximum() != null) {
            return getMaximum().getAmount().compareTo(price.getAmount()) >= 0;
        }

        return true;
    }

    public Element toXml() {
        Document document = DocumentHelper.createDocument();
        Element out = xmlConverter.createRootElement(document, PRICE_RANGE);

        Price min = getMinimum();
        Price max = getMaximum();

        if (null != min) {
            xmlConverter.addElementToRoot(out, PRICE_RANGE_MINIMUM, min.getAmount().toString());
        } else {
            xmlConverter.addElementToRoot(out, PRICE_RANGE_MINIMUM, NULL_STR);
        }
        if (null != max) {
            xmlConverter.addElementToRoot(out, PRICE_RANGE_MAXIMUM, max.getAmount().toString());
        } else {
            xmlConverter.addElementToRoot(out, PRICE_RANGE_MAXIMUM, NULL_STR);
        }
        xmlConverter.printResults(out);
        return out;
    }

    public static PriceRange parse(Element parent) throws IllegalArgumentException {
        if (parent == null || parent.getName() == null) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }
        if (parent.getName().equals(PRICE_RANGE)) {
            String minString = xmlConverter.parse(parent, PRICE_RANGE_MINIMUM);
            Price min = !isMoney(minString) ? null : new Price(minString);

            String maxString = xmlConverter.parse(parent, PRICE_RANGE_MAXIMUM);
            Price max = !isMoney(maxString) ? null : new Price(maxString);
            return new PriceRange(min, max);
        } else {
            Element priceRange = xmlConverter.parseElement(parent, PRICE_RANGE);
            if (priceRange != null) {
                String minString = xmlConverter.parse(priceRange, PRICE_RANGE_MINIMUM);
                Price min = !isMoney(minString) ? null : new Price(minString);

                String maxString = xmlConverter.parse(priceRange, PRICE_RANGE_MAXIMUM);
                Price max = !isMoney(maxString) ? null : new Price(maxString);
                return new PriceRange(min, max);

            } else {
                return new PriceRange(null, null);
            }
        }
    }
}
