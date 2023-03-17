package de.softconex.assessment.calcmodel.dto;

import org.dom4j.Element;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static de.softconex.assessment.calcmodel.conversion.XMLConversionInterface.xmlConverter;
import static de.softconex.assessment.calcmodel.utils.ValidationUtil.*;


/**
 * An immutable {@link Price} object.
 * <p>
 * For simplicity reasons, the price class does not have a currency.
 * <p>
 * Scale can be ignored when checking for equality, e.g. "1.2" is the same as "1.20".
 */
public final class Price {
    public static final String PRICE_ENTITY = "price";
    public static final String PRICE_AMOUNT = "amount";
    public static final Price ZERO_PRICE = new Price("0");
    public static final Price PRICE_99 = new Price("99");
    public static final Price PRICE_100 = new Price("100");
    public static final Price PRICE_199 = new Price("199");

    private final BigDecimal amount;

    public Price() {
        this.amount = null;
    }

    /**
     * Constructs a new {@link Price} instance.
     *
     * @param amount (if null, a NullPointerException will be thrown).
     */
    public Price(BigDecimal amount) {
        this.amount = amount;
        if (!isMoney(this.amount)) {
            throw new IllegalArgumentException(WRONG_PRICE_CONSTRUCTOR_ARGUMENT);
        }

    }

    /**
     * Constructs a new {@link Price} instance.
     *
     * @param amount (if null, a NullPointerException will be thrown).
     */
    public Price(Integer amount) {

        if (!isMoney(amount)) {
            throw new IllegalArgumentException(WRONG_PRICE_CONSTRUCTOR_ARGUMENT);
        }
        this.amount = new BigDecimal(amount);
    }

    /**
     * Constructs a new {@link Price} instance.
     *
     * @param amount (if null, a NullPointerException will be thrown).
     */
    public Price(@NotNull String amount) {
        if (amount.equals(NULL_STR)
                ||
                !isMoney(amount)) {
            throw new IllegalArgumentException(WRONG_PRICE_CONSTRUCTOR_ARGUMENT);
        }
        this.amount = new BigDecimal(amount);
    }

    /**
     * Adds the passed {@link Price} object to "this" object. A new instance of
     * {@link Price} will be returned.
     */
    public Price add(@NotNull Price price) {
        return new Price(getAmount().add(price.getAmount()));
    }


    /**
     * Convert to XML element using the passed elementName.
     */
    public static Element toXml(String amountValue) {
        if (!isMoney(amountValue)) {
            throw new IllegalArgumentException(WRONG_PRICE_CONSTRUCTOR_ARGUMENT);
        }
        final Element resultElement = xmlConverter.toXml(PRICE_ENTITY, PRICE_AMOUNT, amountValue);
        xmlConverter.printResults(resultElement);
        return resultElement;
    }

    public static Element toXml(@NotNull Integer amountValue) {
        if (isMoney(amountValue)) {
            return xmlConverter.toXml(PRICE_ENTITY, PRICE_AMOUNT, new BigDecimal(amountValue).toString());
        } else {
            return xmlConverter.toXml(PRICE_ENTITY, PRICE_AMOUNT, NULL_STR);
        }
    }

    public static Element toXml(@NotNull BigDecimal amountValue) {
        if (isMoney(amountValue)) {
            return xmlConverter.toXml(PRICE_ENTITY, PRICE_AMOUNT, amountValue.toString());
        } else {
            return xmlConverter.toXml(PRICE_ENTITY, PRICE_AMOUNT, NULL_STR);
        }
    }

    /**
     * Parse the passed element.
     *
     * @param element (if element==null, null will be returned).
     */
    public static Price parse(@NotNull Element element, String priceElementName) throws IllegalArgumentException {
        if (element.getName() == null) {
            throw new IllegalArgumentException(WRONG_PRICE_CONSTRUCTOR_ARGUMENT);
        }
        if (element.getName().equals(priceElementName)) {
            return new Price(xmlConverter.parse(element, PRICE_AMOUNT));
        } else {
            Element price = xmlConverter.parseElement(element, priceElementName);
            if (price != null) {
                return Price.parse(price, priceElementName);
            } else {
                return new Price();
            }
        }
    }

    /**
     * Returns the amount; since a {@link BigDecimal} itself is immutable, no
     * defensive copies will be made here.
     */
    public BigDecimal getAmount() {
        return amount;
    }


    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return customEqualsIgnoringScale(obj);
    }

    private boolean customEqualsIgnoringScale(Object obj) {
        if (!(obj instanceof Price)) {
            return false;
        }

        return safeEqualsIgnoreScale(this.getAmount(), ((Price) obj).getAmount());
    }

    @Override
    public int hashCode() {
        return customHashCodeIgnoringScale();
    }

    private int customHashCodeIgnoringScale() {
        return getAmount().setScale(2, RoundingMode.HALF_UP).hashCode();
    }

    @Override
    public String toString() {
        return getAmount() == null ? NULL_STR : getAmount().toString();
    }

    /**
     * Returns whether the two {@link Price} instances are equal allowing null
     * values. If both prices are null, they will be considered equal.
     */
    public static boolean equals(Price price1, Price price2) {
        if (price1 == null && price2 == null) {
            return true;
        }

        if (price1 == null || price2 == null) {
            return false;
        }

        return price1.equals(price2);
    }

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @SuppressWarnings({"NumberEquality", "BigDecimalEquals"})
    private static boolean safeEqualsIgnoreScale(BigDecimal value1, BigDecimal value2) {
        if (value1 == value2) {
            // includes both null
            return true;
        }

        if (value1 == null || value2 == null) {
            // one value null, other value not null
            return false;
        }

        int scale;
        int scale1 = value1.scale();
        int scale2 = value2.scale();

        if (scale1 == scale2) {
            return value1.equals(value2);
        }

        scale = Math.max(scale1, scale2);

        return value1.setScale(scale, ROUNDING_MODE).equals(value2.setScale(scale, ROUNDING_MODE));
    }
}
