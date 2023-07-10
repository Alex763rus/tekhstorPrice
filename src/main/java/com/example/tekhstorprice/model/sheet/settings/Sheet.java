package com.example.tekhstorprice.model.sheet.settings;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Sheet {

    private String spreadsheetId;
    private String range;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sheet sheet = (Sheet) o;
        return Objects.equals(spreadsheetId, sheet.spreadsheetId) && Objects.equals(range, sheet.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spreadsheetId, range);
    }
}
