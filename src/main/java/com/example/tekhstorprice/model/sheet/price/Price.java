package com.example.tekhstorprice.model.sheet.price;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Price {

    private final String name;
    private final Double price2year;
    private final Double priceDrop;
    private final Double priceOpt;

    private final PriceGroup priceGroup;
    private final String priceCommand;
    private final int order;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(priceGroup, price.priceGroup) && Objects.equals(name, price.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priceGroup, name);
    }
}
