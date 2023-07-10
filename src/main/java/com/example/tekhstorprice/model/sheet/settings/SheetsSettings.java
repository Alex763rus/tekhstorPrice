package com.example.tekhstorprice.model.sheet.settings;

import com.example.tekhstorprice.enums.SheetName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString
public class SheetsSettings {

    private Map<String, Sheet> sheets;

    public Sheet getSheet(SheetName sheetName) {
        return sheets.get(sheetName.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetsSettings that = (SheetsSettings) o;
        return Objects.equals(sheets, that.sheets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheets);
    }
}
