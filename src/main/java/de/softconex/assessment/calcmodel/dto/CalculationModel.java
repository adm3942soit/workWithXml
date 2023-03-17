package de.softconex.assessment.calcmodel.dto;

import de.softconex.assessment.calcmodel.CalculationModelDetailList;
import de.softconex.assessment.calcmodel.PriceList;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

import static de.softconex.assessment.calcmodel.conversion.XMLConversionInterface.xmlConverter;
import static de.softconex.assessment.calcmodel.utils.ValidationUtil.NULL_STR;
import static de.softconex.assessment.calcmodel.utils.ValidationUtil.WRONG_CONSTRUCTOR_ARGUMENT;

public class CalculationModel {
    public static final String CALCULATION_MODEL = "calculationModel";
    public static final String CALCULATION_MODEL_DETAIL_LIST = "calculationModelDetailList";
    public static final String FIRST_PERCENT = "firstPercent";

    private CalculationModelDetailList calculationModelDetailList = new CalculationModelDetailList();
    private boolean firstPerCent = false;

    public CalculationModel(BigDecimal perCent, Price absolute, PriceRange priceRange) {
        this.calculationModelDetailList.add(new CalculationModelDetail(perCent, absolute, priceRange));
    }

    public CalculationModel() {
    }


    /**
     * set calculationModel
     */
    public void addCalculationModelDetailList(CalculationModelDetail calculationModelDetail) {
        this.calculationModelDetailList.add( calculationModelDetail);
    }

    public void setCalculationModelDetailList(CalculationModelDetailList calculationModelDetailList) {
        this.calculationModelDetailList = calculationModelDetailList;
    }

    public CalculationModelDetailList getCalculationModelDetailList() {
        return this.calculationModelDetailList;
    }

    public boolean isFirstPerCent() {
        return this.firstPerCent;
    }

    public void setFirstPerCent(boolean firstPerCent) {
        this.firstPerCent = firstPerCent;
    }

    @SuppressWarnings({"unused"})
    public static PriceList calculate(@NotNull Price price,
                                      @NotNull CalculationModelDetailList calculationModelDetailList,
                                      boolean firstPerCent) {
        final PriceList priceList =  new PriceList();
        for (CalculationModelDetail detail : calculationModelDetailList) {
            priceList.add(CalculationModelDetail.calculate(price, detail, firstPerCent));
        }
        return priceList;
    }

    public final PriceList calculate(@NotNull Price price) {

        if (this.getCalculationModelDetailList() == null) {
            throw new IllegalArgumentException("Not given details for calculation!");
        }
        return calculate(price,
                this.getCalculationModelDetailList(),
                this.isFirstPerCent());
    }

    public static Element toXml(@NotNull CalculationModel calculationModel) {
        Document document = DocumentHelper.createDocument();

        Element root = xmlConverter.createRootElement(document, CALCULATION_MODEL);
        addFirstPercentElement(root, calculationModel);
        addCalculationDetailsElement(root, calculationModel);
        xmlConverter.printResults(root);
        return root;
    }

    public static void addFirstPercentElement(@NotNull Element root,
                                              @NotNull CalculationModel calculationModel) {
        if (root.getName().isBlank()) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }
        if (root.getName().equals(CALCULATION_MODEL)) {
            Element firstPercent = root.addElement(FIRST_PERCENT);
            xmlConverter.addElementToRoot(firstPercent, FIRST_PERCENT, String.valueOf(calculationModel.isFirstPerCent()));
            xmlConverter.printResults(firstPercent);
        }
    }

    public static void addCalculationDetailsElement(@NotNull Element root, @NotNull CalculationModel calculationModel) {
        if (root.getName() == null) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }
        if (root.getName().equals(CALCULATION_MODEL)) {
            CalculationModelDetailList calculationModelDetailList = calculationModel.getCalculationModelDetailList();
            Element details = CalculationModelDetailList.toXml(calculationModelDetailList);
            root.add(details);
        }
    }

    public CalculationModel parse(@NotNull Element element) {
        if (element.getName() == null) {
            throw new IllegalArgumentException(WRONG_CONSTRUCTOR_ARGUMENT);
        }
        if (element.getName().equals(CALCULATION_MODEL)) {
            CalculationModel calculationModel = new CalculationModel();
            CalculationModelDetailList modelDetails = CalculationModelDetailList.parse(element);

            calculationModel.setCalculationModelDetailList(modelDetails);

            String firstPercent = xmlConverter.parse(element, FIRST_PERCENT);
            calculationModel.setFirstPerCent(firstPercent != null && firstPercent.equals("true"));

            return calculationModel;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculationModel)) return false;
        CalculationModel that = (CalculationModel) o;
        return isFirstPerCent() == that.isFirstPerCent() && Objects.equals(getCalculationModelDetailList(), that.getCalculationModelDetailList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCalculationModelDetailList(), isFirstPerCent());
    }

    @Override
    public String toString() {
        return "CalculationModel{" +
                "calculationModelDetail="
                + (getCalculationModelDetailList() == null ? NULL_STR : getCalculationModelDetailList().toString()) +
                ", firstPerCent=" + isFirstPerCent() +
                '}';
    }
}
