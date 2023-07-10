package com.example.tekhstorprice.enums;

public enum UserRole {

    BLOCKED("Заблокирован"),
    CLIENT("Клиент"),
    ADMIN("Администратор");

    private String title;

    UserRole(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
