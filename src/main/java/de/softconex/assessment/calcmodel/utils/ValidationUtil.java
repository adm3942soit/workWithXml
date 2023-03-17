package de.softconex.assessment.calcmodel.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;

public class ValidationUtil {
    public static final String WRONG_CONSTRUCTOR_ARGUMENT = "Tried to construct a new object from null element!";

    public static final String WRONG_PRICE_CONSTRUCTOR_ARGUMENT = "Tried to construct a new Price objects with string amount==null or empty or has not numeric symbols!";
    public static final String NEGATIVE_PRICE = "Price can't be started with '-'!";
    public static final String BAD_PRICE = "Percent can't be like ";
    public static final String NULL_PRICE = "Price is null!";
    public static final String NULL_PERCENT = "Percent is null!";
    public static final String NULL_STR = "null";
    private static final Log logger = LogFactory.getLog(ValidationUtil.class);

    private ValidationUtil() {
    }

    public static boolean isMoney(Integer money) {
        if (money == null) {
            logger.error(NULL_PRICE);
            return false;
        }
        return isMoney(money.toString());
    }

    public static boolean isMoney(BigDecimal money) {
        if (money == null) {
            logger.error(NULL_PRICE);
            return false;
        }
        return isMoney(money.toString());
    }

    public static boolean isMoney(String money) {
        if (StringUtils.isBlank(money)) {
            logger.error(NULL_PRICE);
            return false;
        }

        money = money.trim();

        if (money.startsWith("-")) {
            logger.error(NEGATIVE_PRICE);
            return (false);
        }

        if (money.length() > 0
                && money.trim().matches("^(\\d*(\\.\\d\\d?)?|\\d+)$")) {
            return (true);
        } else {
            logger.error(BAD_PRICE + money);
            return (false);
        }
    }
    public static boolean isPrice(String price){
        if (StringUtils.isBlank(price) || price.equals(NULL_STR)) {
            logger.error(NULL_PERCENT);
            return false;
        }
        price = price.trim();

        if (price.startsWith("-")) {
            logger.error(NEGATIVE_PRICE);
            return (false);
        }

        if (price.length() > 0
                && price.trim().matches("^(\\d*(\\.\\d\\d?)?|\\d+)$")) {
            return (true);
        } else {
            logger.error(BAD_PRICE + price);
            return (false);
        }

    }

    public static boolean isPercent(String percent) {
        if (StringUtils.isBlank(percent) || percent.equals(NULL_STR)) {
            logger.error(NULL_PERCENT);
            return false;
        }
        percent = percent.trim();

        if (percent.startsWith("-")) {
            logger.error(NEGATIVE_PRICE);
            return (false);
        }

        if (percent.length() > 0
                && percent.trim().matches("^(\\d*(\\.\\d\\d?)?|\\d+)$")) {
            return (true);
        } else {
            logger.error(BAD_PRICE + percent);
            return (false);
        }
    }

}
