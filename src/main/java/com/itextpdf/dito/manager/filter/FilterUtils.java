package com.itextpdf.dito.manager.filter;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class FilterUtils {

    public static String getStringFromFilter(final String value) {
        return StringUtils.isEmpty(value)
                ? ""
                : value.toLowerCase();
    }

    public static Boolean getBooleanMultiselectFromFilter(final List<Boolean> values) {
        return (values == null || values.size() > 1) ? null : values.get(0);
    }

    public static Date getStartDateFromRange(final List<String> dates) {
        validateDateRangeSize(dates);
        return dates != null ? getDateFromFilter(dates.get(0)) : null;
    }

    public static Date getEndDateFromRange(final List<String> dates) {
        validateDateRangeSize(dates);
        return dates != null ? getDateFromFilter(dates.get(1)) : null;
    }

    private static void validateDateRangeSize(List<String> dates) {
        if (dates != null && dates.size() != 2) {
            throw new IllegalArgumentException("Date range should contain two elements: start date and end date");
        }
    }

    private static Date getDateFromFilter(final String value) {
        final Date result;
        final DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            result = format.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(new StringBuilder().append("Invalid date param:").append(value).toString());
        }
        return result;
    }

    private FilterUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
