package com.example.tekhstorprice.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constant {
    @NoArgsConstructor(access = PRIVATE)
    public final class  App{

    }

    @NoArgsConstructor(access = PRIVATE)
    public final class Command {
        public static final String COMMAND_DEFAULT = "/default";
        public static final String COMMAND_START = "/start";

        public static final String COMMAND_CHECK_PRICE = "/check_price";

        public static final String COMMAND_UPDATE_PRICE = "/update_price";
        public static final String COMMAND_VIEW_CLIENT = "/view_client";

        public static final String COMMAND_CONTACT_MANAGER = "/contact_manager";

        public static final String COMMAND_BACK = "/back";

    }



    public static String APP_NAME = "tekhstorPrice";
    public static String PARSE_MODE = "Markdown";

    public static String USER_DIR = "user.dir";
    public static String WHITE_LIST_FILE_NAME = "WhiteListUsers.json";
    public static String SHEETS_SETTINGS = "SheetsSettings.json";
    public static final String SHIELD = "\\";
    public static final String EMPTY = "";

    public static final String STAR = "*";

    public static String NEW_LINE = "\n";
    public static String SPACE = " ";
    public static String SHEET_RESULT_NAME = "ИМПОРТ";



}
