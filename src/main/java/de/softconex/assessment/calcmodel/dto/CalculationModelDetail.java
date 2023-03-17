package de.softconex.assessment.calcmodel.dto;

import de.softconex.assessment.calcmodel.CalculationModelDetailList;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

import static de.softconex.assessment.calcmodel.conversion.XMLConversionInterface.xmlConverter;
import static de.softconex.assessment.calcmodel.dto.Price.ZERO_PRICE;
import static de.softconex.assessment.calcmodel.dto.PriceRange.*;
import static de.softconex.assessment.calcmodel.utils.ValidationUtil.*;

public class CalculationModelDetail {
    public static final String CALCULATION_MODEL_DETAIL = "calculationModelDetail";
    public static final String CALCULATION_MODEL_DETAIL_PERCENT = "percent";
    public static final String ABSOLUTE_PRICE = "absolute";
    public static final String PRICE_RANGE = "priceRange";
    public static final BigDecimal MARKUP_0_99 = new BigDecimal(10);
    public static final Price PRICE_MARKUP_0_99 = new Price(MARKUP_0_99);
    public static final BigDecimal MARKUP_100_199 = new BigDecimal(15);
    public static final Price PRICE_MARKUP_100_199 = new Price(MARKUP_100_199);
    public static final BigDecimal PERCENT_MARKUP_GREATER_199 = new BigDecimal(8);


    private BigDecimal perCent = null;
    private Price absolute = null;
    private PriceRange priceRange = null;

    public CalculationModelDetail(BigDecimal perCent, Price absolute, PriceRange priceRange) {
        this.perCent = perCent;
        this.absolute = absolute;
        this.priceRange = priceRange;
    }

    public CalculationModelDetail() {
    }

    /*
     * If price is between 0 and 99 EUR, a markup of 10 EUR will be applied
     * If price is between 100 and 199 EUR, a markup of 15 EUR will be applied
     * If price is higher than 199 EUR, a markup of 8% will be applied
     * support firstPerCent method argument
     * support perCent attribute
     * checked if the price is inside the passed range, if not, throw an IllegalArgumentException
     */
    public static Price calculate(@NotNull Price priceIn, @NotNull CalculationModelDetail calculationModelDetail, boolean firstPerCent) {

        Price out = priceIn;
        if (calculationModelDetail.getAbsolute() != null) {
            out = priceIn.add(calculationModelDetail.getAbsolute());
        }

        if (calculationModelDetail.getPriceRange() != null && !calculationModelDetail.getPriceRange().contains(priceIn)) {
            throw new IllegalArgumentException("Price out of range!");
        }

        if (PRICE_RANGE_0_99.contains(out)) {
            out = calculatePercentage(out, firstPerCent, calculationModelDetail.getPerCent(), PRICE_MARKUP_0_99);
        } else if (PRICE_RANGE_100_199.contains(out)) {
            out = calculatePercentage(out, firstPerCent, calculationModelDetail.getPerCent(), PRICE_MARKUP_100_199);
        } else if (PRICE_RANGE_GREATER_199.contains(out)) {
            out = calculatePercentage(out, firstPerCent, PERCENT_MARKUP_GREATER_199, ZERO_PRICE);
        }
        return out;
    }

    public final Price calculate(@NotNull Price priceIn, boolean firstPerCent) {

        Price out = priceIn;
        if (this.getAbsolute() != null) {
            out = priceIn.add(this.getAbsolute());
        }
        if (this.getPriceRange() != null && !this.getPriceRange().contains(priceIn)) {
            throw new IllegalArgumentException("Price out of range!");
        }

        if (PRICE_RANGE_0_99.contains(out)) {
            out = calculatePercentage(out, firstPerCent, this.getPerCent(), PRICE_MARKUP_0_99);
        } else if (PRICE_RANGE_100_199.contains(out)) {
            out = calculatePercentage(out, firstPerCent, this.getPerCent(), PRICE_MARKUP_100_199);
        } else if (PRICE_RANGE_GREATER_199.contains(out)) {
            out = calculatePercentage(out, firstPerCent, PERCENT_MARKUP_GREATER_199, ZERO_PRICE);
        }
        return out;
    }

    public static Price calculatePercentage(@NotNull Price price, boolean firstPerCent, BigDecimal perCent, @NotNull Price markup) {

        if (firstPerCent) {
            //firstPerCent=true*: ( 100 + 10% ) + 10 = 120
            if (perCent != null) {
                BigDecimal calculatedPerCent = BigDecimal.valueOf(percent(price.getAmount().doubleValue(), perCent.doubleValue()));

                return price.add(new Price(calculatedPerCent)).add(markup);
            } else {
                return price.add(markup);
            }
        } else {//firstPerCent=false*: ( 100 + 10 ) + 10% = 121
            final Price out = price.add(markup);
            if (perCent != null) {
                BigDecimal calculatedPerCent = BigDecimal.valueOf(percent(out.getAmount().doubleValue(), perCent.doubleValue()));
                return out.add(new Price(calculatedPerCent));
            } else {
                return out;
            }

        }
    }

    private static double percent(double obtained, double percent) {
        return obtained * (percent / 100.00);
    }

    public Element toXml() {
        CalculationModelDetail calculationModelDetail = new CalculationModelDetail(getPerCent(), getAbsolute(), getPriceRange());
        return toXml(calculationModelDetail);
    }


    public static Element toXml(CalculationModelDetail calculationModelDetail) {
        Document document = DocumentHelper.createDocument();

        Element root = xmlConverter.createRootElement(document, CALCULATION_MODEL_DETAIL);

        addPercentElement(root, calculationModelDetail);
        addAbsoluteElement(root, calculationModelDetail);
        addPriceRangeElement(root, calculationModelDetail);
        xmlConverter.printResults(root);

        return root;
    }


    public static CalculationModelDetail parse(@NotNull Element element) {
        if (element.getName().isBlank()) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }
        CalculationModelDetail calculationModelDetail = new CalculationModelDetail();
        if (element.getName().equals(CALCULATION_MODEL_DETAIL)) {
            addPercentToModel(element, calculationModelDetail);
            addAbsoluteToModel(element, calculationModelDetail);
            addPriceRangeToModel(element, calculationModelDetail);
        } else {
            Element detail = xmlConverter.parseElement(element, CALCULATION_MODEL_DETAIL);
            if (detail != null) {
                addPercentToModel(detail, calculationModelDetail);
                addAbsoluteToModel(detail, calculationModelDetail);
                addPriceRangeToModel(detail, calculationModelDetail);
            }
        }
        return calculationModelDetail;
    }

    public static void addPercentElement(@NotNull Element root, @NotNull CalculationModelDetail calculationModelDetail) {
        if (root.getName().isBlank()) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }
        if (root.getName().equals(CALCULATION_MODEL_DETAIL)) {

            Element percent = root.addElement(CALCULATION_MODEL_DETAIL_PERCENT);
            if (isPercent(String.valueOf(calculationModelDetail.getPerCent()))) {
                xmlConverter.addElementToRoot(percent, CALCULATION_MODEL_DETAIL_PERCENT, String.valueOf(calculationModelDetail.getPerCent()));
            } else {
                xmlConverter.addElementToRoot(percent, CALCULATION_MODEL_DETAIL_PERCENT, NULL_STR);
            }
            xmlConverter.printResults(percent);
        }else {
            Element detail = xmlConverter.parseElement(root, CALCULATION_MODEL_DETAIL);
            if(detail!=null){
                Element percent = root.addElement(CALCULATION_MODEL_DETAIL_PERCENT);
                if (isPercent(String.valueOf(calculationModelDetail.getPerCent()))) {
                    xmlConverter.addElementToRoot(percent, CALCULATION_MODEL_DETAIL_PERCENT, String.valueOf(calculationModelDetail.getPerCent()));
                } else {
                    xmlConverter.addElementToRoot(percent, CALCULATION_MODEL_DETAIL_PERCENT, NULL_STR);
                }
                xmlConverter.printResults(percent);
            }
        }
    }

    public static void addAbsoluteElement(@NotNull Element root, @NotNull CalculationModelDetail calculationModelDetail) {
        Element absolute = root.addElement(ABSOLUTE_PRICE);

        if (calculationModelDetail.getAbsolute() != null
                && calculationModelDetail.getAbsolute().getAmount() != null
                && isPrice(calculationModelDetail.getAbsolute().getAmount().toString())) {
            xmlConverter.addElementToRoot(absolute, Price.PRICE_AMOUNT, calculationModelDetail.getAbsolute().getAmount().toString());
        } else {
            xmlConverter.addElementToRoot(absolute, Price.PRICE_AMOUNT, NULL_STR);
        }
        xmlConverter.printResults(absolute);
    }

    public static void addPriceRangeElement(@NotNull Element root, @NotNull CalculationModelDetail calculationModelDetail) {
        Element priceRangeElement = root.addElement(PRICE_RANGE);

        if (calculationModelDetail.getPriceRange() != null) {
            if (isMoney(calculationModelDetail.getPriceRange().getMinimum().getAmount())) {
                xmlConverter.addElementToRoot(priceRangeElement,
                        PRICE_RANGE_MINIMUM, calculationModelDetail.getPriceRange().getMinimum().getAmount().toString());
            } else {
                xmlConverter.addElementToRoot(priceRangeElement,
                        PRICE_RANGE_MINIMUM, NULL_STR);
            }
            if (isMoney(calculationModelDetail.getPriceRange().getMaximum().getAmount())) {
                xmlConverter.addElementToRoot(priceRangeElement,
                        PRICE_RANGE_MAXIMUM, calculationModelDetail.getPriceRange().getMaximum().getAmount().toString());
            } else {
                xmlConverter.addElementToRoot(priceRangeElement,
                        PRICE_RANGE_MAXIMUM, NULL_STR);
            }
        } else {
            xmlConverter.addElementToRoot(priceRangeElement, PRICE_RANGE_MINIMUM, NULL_STR);
            xmlConverter.addElementToRoot(priceRangeElement, PRICE_RANGE_MAXIMUM, NULL_STR);
        }
        xmlConverter.printResults(priceRangeElement);
    }

    public static void addPercentToModel(@NotNull Element element, @NotNull CalculationModelDetail calculationModelDetail) {
        if (element.getName() == null) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }

        if (element.getName().equals(CALCULATION_MODEL_DETAIL)) {
            String percent = xmlConverter.parse(element, CALCULATION_MODEL_DETAIL_PERCENT);
            if (isPercent(percent)) {
                calculationModelDetail.setPerCent(BigDecimal.valueOf(Integer.parseInt(percent)));
            } else {
                calculationModelDetail.setPerCent(null);
            }
        } else {
            Element details = xmlConverter.parseElement(element, CALCULATION_MODEL_DETAIL);
            if (details != null) {
                String percent = xmlConverter.parse(details, CALCULATION_MODEL_DETAIL_PERCENT);
                if (isPercent(percent)) {
                    calculationModelDetail.setPerCent(BigDecimal.valueOf(Integer.parseInt(percent)));
                } else {
                    calculationModelDetail.setPerCent(null);
                }
            } else {
                calculationModelDetail.setPerCent(null);
            }

        }

    }

    public static void addAbsoluteToModel(@NotNull Element element, @NotNull CalculationModelDetail calculationModelDetail) {
        if (element.getName() == null) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }

        if (element.getName().equals(CALCULATION_MODEL_DETAIL)) {
            Price absolutePrice = Price.parse(element, ABSOLUTE_PRICE);
            calculationModelDetail.setAbsolute(absolutePrice);
        } else {
            Element details = xmlConverter.parseElement(element, CALCULATION_MODEL_DETAIL);
            if (details != null) {
                Price absolutePrice = Price.parse(details, ABSOLUTE_PRICE);
                calculationModelDetail.setAbsolute(absolutePrice);
            } else calculationModelDetail.setAbsolute(null);
        }
    }

    public static void addPriceRangeToModel(@NotNull Element element, @NotNull CalculationModelDetail calculationModelDetail) {
        if (element.getName() == null) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }

        if (element.getName().equals(CALCULATION_MODEL_DETAIL)) {
            PriceRange priceRange = PriceRange.parse(element);
            calculationModelDetail.setPriceRange(priceRange);
        } else {
            Element details = xmlConverter.parseElement(element, CALCULATION_MODEL_DETAIL);
            if (details != null) {
                PriceRange priceRange = PriceRange.parse(details);
                calculationModelDetail.setPriceRange(priceRange);
            } else {
                calculationModelDetail.setPriceRange(null);
            }
        }
    }

    public BigDecimal getPerCent() {
        return this.perCent;
    }

    public void setPerCent(BigDecimal perCent) {
        this.perCent = perCent;
    }

    public Price getAbsolute() {
        return this.absolute;
    }

    public void setAbsolute(Price absolute) {
        this.absolute = absolute;
    }

    public PriceRange getPriceRange() {
        return this.priceRange;
    }

    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    @Override
    public String toString() {
        String min = (getPriceRange() != null && getPriceRange().getMinimum() != null) ? getPriceRange().getMinimum().toString() : NULL_STR;
        String max = (getPriceRange() != null && getPriceRange().getMaximum() != null) ? getPriceRange().getMaximum().toString() : NULL_STR;
        return "CalculationModelDetail: "
                + (getAbsolute() == null ? NULL_STR : getAbsolute().toString()) + ", "
                + (getPerCent() == null ? NULL_STR : getPerCent().toString()) + "%"
                + "; priceRange: " + min + " - " + max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculationModelDetail)) return false;
        CalculationModelDetail that = (CalculationModelDetail) o;
        return Objects.equals(getPerCent(), that.getPerCent()) && Objects.equals(getAbsolute(), that.getAbsolute()) && Objects.equals(getPriceRange(), that.getPriceRange());
    }

    @Override
    public int hashCode() {
        return Objects.hash(perCent, absolute, priceRange);
    }

}
