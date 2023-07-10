package com.example.tekhstorprice.exception;

public class ExcelGenerationException extends RuntimeException {
    private final static String EXCEL_GENERATION_ERROR = "Ошибка формирования файла: ";
    public ExcelGenerationException(String message) {
        super(EXCEL_GENERATION_ERROR + message);
    }
}
