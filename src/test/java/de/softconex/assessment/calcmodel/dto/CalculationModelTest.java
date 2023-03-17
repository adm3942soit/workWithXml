package de.softconex.assessment.calcmodel.dto;

import de.softconex.assessment.calcmodel.CalculationModelDetailList;
import de.softconex.assessment.calcmodel.PriceList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.softconex.assessment.calcmodel.dto.CalculationModel.toXml;
import static de.softconex.assessment.calcmodel.dto.CalculationModelDetail.*;
import static de.softconex.assessment.calcmodel.dto.Price.ZERO_PRICE;

class CalculationModelTest extends Assertions {
    private static final Log LOG = LogFactory.getLog(CalculationModelTest.class);
    CalculationModelDetailList calculationModelDetailList;
    CalculationModelDetail calculationModelDetail;
    CalculationModel calculationModel;
    Price price;
    Price price1;
    PriceRange priceRange;

    @BeforeEach
    void setUp() {
        calculationModelDetailList = new CalculationModelDetailList();
        calculationModel = new CalculationModel();
        calculationModel.setFirstPerCent(false);
        price = new Price("1");
        price1 = new Price("100");
        priceRange = new PriceRange(price, price1);
        calculationModelDetail = new CalculationModelDetail();
        calculationModelDetail.setAbsolute(price);
        calculationModelDetail.setPriceRange(priceRange);
        calculationModelDetail.setPerCent(new BigDecimal("10.0"));
    }

    @AfterEach
    void tearDown() {
        calculationModelDetailList = null;
        calculationModelDetail = null;
        calculationModel = null;
        price = null;
        price1 = null;
        priceRange = null;

    }


    @Test
    void toXmlNullDetails() {
        calculationModel.setCalculationModelDetailList(null);

        assertThrows(NullPointerException.class, () -> {
            toXml(calculationModel);
        });
        calculationModel.setCalculationModelDetailList(calculationModelDetailList);



        Element writtenElement = toXml(calculationModel);

        LOG.info("written: " + writtenElement.asXML());
        Assertions.<NullPointerException>assertThrows(NullPointerException.class, () -> {
            calculationModel.parse(writtenElement);
        });

    }

    @Test
    void toXmlNullAbsolute() {
        calculationModelDetail.setAbsolute(null);
        calculationModelDetail.setPriceRange(priceRange);
        calculationModelDetail.setPerCent(CalculationModelDetail.PERCENT_MARKUP_GREATER_199);
        calculationModelDetailList.add(calculationModelDetail);
        calculationModel.setCalculationModelDetailList(calculationModelDetailList);
        final Element writtenElement = toXml(calculationModel);
        LOG.info("written: " + writtenElement.asXML());

        assertThrows(NullPointerException.class, () -> calculationModel.parse(writtenElement));

    }

    @Test
    void toXmlNotNull() {
        calculationModelDetailList = new CalculationModelDetailList();
        calculationModel = new CalculationModel();
        price = new Price("1.0");
        price1 = new Price("100.0");
        priceRange = new PriceRange(price, price1);

        calculationModelDetail.setPerCent(CalculationModelDetail.PERCENT_MARKUP_GREATER_199);
        calculationModelDetail.setPriceRange(priceRange);
        calculationModelDetail.setAbsolute(price);
        calculationModelDetailList.add(calculationModelDetail);

        calculationModel.setCalculationModelDetailList(calculationModelDetailList);
        calculationModel.setFirstPerCent(true);

        final Element writtenElement = toXml(calculationModel);
        LOG.info("written: " + writtenElement.asXML());
        final CalculationModel parsed = calculationModel.parse(writtenElement);
        assertNotNull(parsed);
        assertEquals(parsed, calculationModel);

    }

    @Test
    void calculateAbsoluteOnly() {
        final CalculationModelDetail detail = new CalculationModelDetail();
        detail.setAbsolute(new Price(5));
        detail.setPerCent(null);
        final CalculationModel calculationModel = new CalculationModel();
        calculationModelDetailList.add(detail);
        calculationModel.setCalculationModelDetailList(calculationModelDetailList);
        calculationModel.setFirstPerCent(true);
        Price in = new Price(1);

        PriceList calculated = calculationModel.calculate(in);
        //markup == 10
        assertEquals(new Price(6).add(PRICE_MARKUP_0_99), calculated.get(0));


        in = new Price(100);
        Price calculatedPrice = detail.calculate(in, true);
        assertEquals(new Price(105).add(PRICE_MARKUP_100_199), calculatedPrice);

        in = new Price(200);
        calculatedPrice = detail.calculate(in, true);
        Price res = calculatePercentage(new Price(205), true, PERCENT_MARKUP_GREATER_199, ZERO_PRICE);
        assertEquals(res, calculatedPrice);

    }

    @Test
    void calculateAbsoluteAndPerCentNull() {
        final CalculationModelDetail detail = new CalculationModelDetail();
        detail.setAbsolute(null);
        detail.setPerCent(null);
        detail.setPriceRange(new PriceRange(null, null));
        final CalculationModel calculationModel = new CalculationModel();
        calculationModelDetailList.add(detail);
        calculationModel.setCalculationModelDetailList(calculationModelDetailList);
        calculationModel.setFirstPerCent(true);
        final Price in = new Price(1);

        final PriceList calculated = calculationModel.calculate(in);
        Price result = new Price(1).add(PRICE_MARKUP_0_99);
        PriceList priceList = new PriceList();
        priceList.add(result);
        assertEquals(priceList, calculated);
    }

}