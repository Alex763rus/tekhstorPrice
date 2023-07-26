package com.example.tekhstorprice.enums;

public enum SheetName {
    APPLE("Прайс Apple"),
    XIAOMI("Прайс Xiaomi"),
    SAMSUNG("Прайс Samsung"),
    DYSON("Прайс Dyson"),
    CONSOLES("Игровые приставки"),
    LEGO("Lego"),
    PAD("Планшеты"),
    HEADPHONES("Наушники"),
    WATCH("Часы и браслеты"),
    SPEAKERS("Умные колонки"),
    HOME("Умный дом"),
    VIDEOCARD("Видеокарты"),
    LAPTOP("Ноутбуки, аксессуары"),
    PRINTER("Принтеры, МФУ"),
    PROCESSOR("Процессоры"),
    TV("Телевизоры");

    private String title;

    SheetName(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static SheetName valueOfCommand(String command) {
        try {
            return valueOf(command.replaceAll("/", "").toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public String getCommandName() {
        return "/" + super.name().toLowerCase();
    }
}
