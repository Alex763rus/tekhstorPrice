package com.example.tekhstorprice.exception;

public class GoogleSheetException extends RuntimeException {
    private final static String EXCEL_READ_GOOGLE_SHEET = "Ошибка чтения google sheet: ";
    public GoogleSheetException(String message) {
        super(EXCEL_READ_GOOGLE_SHEET + message);
    }
}
