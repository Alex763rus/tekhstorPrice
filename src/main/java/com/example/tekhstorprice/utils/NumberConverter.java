package com.example.tekhstorprice.utils;

import lombok.val;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberConverter {

    public static final String LANGUAGE = "ru";
    public static final Locale locale = new Locale(LANGUAGE);


    public static String formatPrice(Double value) {
        val formatter = NumberFormat.getInstance(locale);
        return formatter.format(value);
    }
}
