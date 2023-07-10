package com.example.tekhstorprice.enums;

public enum SheetName {
    APPLE("Прайс Apple"),
    XIAOMI("Прайс Xiaomi");

    private String title;

    SheetName(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static SheetName valueOfCommand(String command){
        return valueOf(command.replaceAll("/", "").toUpperCase());
    }
    public String getCommandName(){
        return "/" + super.name().toLowerCase();
    }
}
