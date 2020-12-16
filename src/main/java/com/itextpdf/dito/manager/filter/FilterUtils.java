package com.itextpdf.dito.manager.filter;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static List<Date> getDateRangeFromFilter(final List<String> dates) {
        return dates != null
                ? dates.stream().map(FilterUtils::getDateFromFilter).collect(Collectors.toList())
                : null;
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
