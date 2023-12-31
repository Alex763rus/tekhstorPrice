package com.example.tekhstorprice.model.sheet.price;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PriceGroup {

    private final String groupName;

    private final String groupCommand;

    private int order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceGroup that = (PriceGroup) o;
        return Objects.equals(groupCommand, that.groupCommand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupCommand);
    }
}
