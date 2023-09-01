package com.example.tekhstorprice.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constant {
    @NoArgsConstructor(access = PRIVATE)
    public final class App {
        public static final String APP_NAME = "tekhstorPrice";
        public static final String USER_DIR = "user.dir";
        public static final String WHITE_LIST_FILE_NAME = "WhiteListUsers.json";
        public static final String SHEETS_SETTINGS = "SheetsSettings.json";
        public static final String SHEET_RESULT_NAME = "ИМПОРТ";
    }

    @NoArgsConstructor(access = PRIVATE)
    public final class Command {

        public static final String COMMAND_CHECK_PRICE = "/check_price";

        public static final String COMMAND_UPDATE_PRICE = "/update_price";

        public static final String COMMAND_VIEW_CLIENT = "/view_client";

        public static final String COMMAND_CONTACT_MANAGER = "/contact_manager";

        public static final String COMMAND_BACK = "/back";

    }

}
