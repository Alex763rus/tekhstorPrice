package com.example.tekhstorprice.utils;

import lombok.val;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    public static final String TEMPLATE_DATE_TIME_SLASH = "MM/dd/yy HH:mm";
    public static final String TEMPLATE_DATE_TIME_DOT = "dd.MM.yyyy H:mm";
    public static final String TEMPLATE_TIME = "H:mm";
    public static final String TEMPLATE_DATE_SLASH = "MM/dd/yy";
    public static final String TEMPLATE_DATE = "ddMMyy";
    public static final String TEMPLATE_DATE_DOT = "dd.MM.yyyy";
    public static final String TEMPLATE_DATE_FILE_NAME = "ddMMyyyy_HHmm_";


    public static Date convertDateFormat(String dateIn, String templateIn) throws ParseException {
        return new SimpleDateFormat(templateIn).parse(dateIn);
    }

    public static String convertDateFormat(String dateIn, String templateIn, String templateOut) throws ParseException {
        val date = convertDateFormat(dateIn, templateIn);
        return new SimpleDateFormat(templateOut).format(date);
    }

    public static String convertDateFormat(Date dateIn, String templateOut) throws ParseException {
        if (dateIn == null) {
            return "";
        }
        return new SimpleDateFormat(templateOut).format(dateIn);
    }

}
