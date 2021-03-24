package com.itextpdf.dito.manager.filter;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FilterUtils {

    public static Long getLongFromFilter(final Long value) {
        return Objects.isNull(value)
                ? 0
                : value;
    }

    public static String getStringFromFilter(final String value) {
        return StringUtils.isEmpty(value)
                ? ""
                : value.toLowerCase();
    }

    public static List<String> getListLowerStringsFromFilter(final List<String> values) {
        return StringUtils.isEmpty(values)
                ?  null
                : values.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public static List<String> getListLowerStringsForNativeFromFilter(final List<String> values) {
        return StringUtils.isEmpty(values)
                ? new ArrayList<>()
                : values.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public static List<String> getListStringsFromFilter(final List<String> values) {
        return Objects.isNull(values)
                ? new ArrayList<>()
                : values;
    }

    public static Boolean getBooleanMultiselectFromFilter(final List<Boolean> values) {
        return (values == null || values.size() > 1) ? null : values.get(0);
    }

    public static String getStringFromMultiselectBooleanFilter(final List<Boolean> values) {
        return (values != null && values.size() == 1) ? values.get(0).toString() : "";
    }

    public static Date getStartDateFromRange(final List<String> dates) {
        return getDateFromFilter(dates.get(0));
    }

    public static Date getEndDateFromRange(final List<String> dates) {
        final Date endDate = getDateFromFilter(dates.get(1));
        return addDayToDate(endDate);
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

    private static Date addDayToDate(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    private FilterUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static String getStringFromLong(Long filter) {
        return Optional.ofNullable(filter).map(Object::toString).orElse("");
    }
}
