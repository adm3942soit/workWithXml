package de.softconex.assessment.calcmodel;

import de.softconex.assessment.calcmodel.dto.CalculationModelDetail;
import de.softconex.assessment.calcmodel.dto.Price;
import de.softconex.assessment.calcmodel.dto.PriceRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static de.softconex.assessment.calcmodel.conversion.XMLConversionInterface.xmlConverter;
import static de.softconex.assessment.calcmodel.dto.CalculationModelDetail.CALCULATION_MODEL_DETAIL;
import static de.softconex.assessment.calcmodel.utils.ValidationUtil.NULL_STR;

/**
 * List of {@link CalculationModelDetail} objects.
 */
//@SuppressWarnings("serial")
public class CalculationModelDetailList extends ArrayList<CalculationModelDetail> {

    public static final String CALCULATION_MODEL_DETAILS_LIST = "calculationModelDetailsList";
    private static final Log logger = LogFactory.getLog(CalculationModelDetailList.class);

    public CalculationModelDetail find(Price price) {
        return this.stream()
                .filter((CalculationModelDetail d) -> d.getPriceRange() == null || d.getPriceRange().contains(price))
                .findFirst().orElse(null);
    }

    public void sortByMinimumAscending() {
        sort(CalculationModelDetailList::compareDetail);
    }

    private static int compareDetail(CalculationModelDetail c1, CalculationModelDetail c2) {
        return compare(c1.getPriceRange(), c2.getPriceRange());
    }

    private static int compare(PriceRange p1, PriceRange p2) {
        return compare(p1.getMinimum(), p2.getMinimum());
    }

    private static int compare(Price p1, Price p2) {
        if (p1 == null || p2 == null) return -1;

        return p1.getAmount().compareTo(p2.getAmount());
    }

    public static Element toXml(List<CalculationModelDetail> models) {
        Document document = DocumentHelper.createDocument();
        Element root = xmlConverter.createRootElement(document, CALCULATION_MODEL_DETAILS_LIST);

        if (models.isEmpty()) {
            logger.error("Models list is empty!");
            root.addElement(CALCULATION_MODEL_DETAIL, NULL_STR);
            return root;
        }

        for (CalculationModelDetail modelDetails : models) {
            Element details = CalculationModelDetail.toXml(modelDetails);
            root.add(details);
        }
        xmlConverter.printResults(root);
        return root;
    }

    public static CalculationModelDetailList parse(@NotNull Element element) {
        CalculationModelDetailList detailList = new CalculationModelDetailList();

        Iterator<?> it = element.nodeIterator();
        while (it.hasNext()) {
            Element current = (Element) it.next();
            if(current.getName().equals(CALCULATION_MODEL_DETAIL)) {
                CalculationModelDetail calculationModelDetail = CalculationModelDetail.parse(current);
                detailList.add(calculationModelDetail);
            }else {
                Element detail = xmlConverter.parseElement(current, CALCULATION_MODEL_DETAIL);
                CalculationModelDetail calculationModelDetail = CalculationModelDetail.parse(detail);
                detailList.add(calculationModelDetail);
            }
        }
        return detailList;
    }
}
