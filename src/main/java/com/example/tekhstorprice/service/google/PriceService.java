package com.example.tekhstorprice.service.google;

import com.example.tekhstorprice.enums.SheetName;
import com.example.tekhstorprice.model.sheet.price.Price;
import com.example.tekhstorprice.model.sheet.price.PriceGroup;
import com.ibm.icu.text.Transliterator;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.tekhstorprice.enums.SheetName.APPLE;
import static com.example.tekhstorprice.enums.SheetName.XIAOMI;
import static com.example.tekhstorprice.utils.StringUtils.prepareShield;

@Service
public class PriceService {

    private Transliterator toLatinTrans;

    @Autowired
    protected GoogleSheetsService googleSheetsService;


    private Map<SheetName, Map<String, Price>> price;

    @PostConstruct
    public void init() {
        toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");
        updatePrice();
    }

    public String getPriceInfo() {
        StringBuilder priceInfo = new StringBuilder();
        priceInfo.append("Иформация о справочнике:")
                .append(" - загружено файлов:" + price.size())
                .append(" - всего товаров: " + price.values().size());
        return priceInfo.toString();
    }

    public Map<String, Price> getPrice(SheetName sheetName) {
        return price.get(sheetName);
    }

    public void updatePrice() {
        price = new HashMap<>();
        price.put(APPLE, createPrice(googleSheetsService.getSheetData(APPLE)));
        price.put(XIAOMI, createPrice(googleSheetsService.getSheetData(XIAOMI)));
    }

    private Map<String, Price> createPrice(List<List<Object>> sheet) {
        val price = new HashMap<String, Price>();
        int y = getFirstHeaderIndex(sheet);
        PriceGroup priceGroup = null;
        int groupCounter = 1;
        int positionInGroup = 1;
        for (; y < sheet.size(); y++) {
            val line = sheet.get(y);
            if (isPrice(line)) {
                val pricePojo = Price.builder()
                        .priceGroup(priceGroup)
                        .name(line.get(0).toString())
                        .command(priceGroup.getCommand() + "_" + positionInGroup)
                        .price2year(getDouble(line, 1))
                        .priceDrop(getDouble(line, 2))
                        .priceOpt(getDouble(line, 3))
                        .order(positionInGroup)
                        .build();
                ++positionInGroup;
                price.put(prepareLink(pricePojo.getName()), pricePojo);
            } else {
                priceGroup = PriceGroup.builder()
                        .groupName(line.get(0).toString())
                        .command(prepareLink(line.get(0).toString()))
                        .order(groupCounter)
                        .build();
                positionInGroup = 1;
                ++groupCounter;
            }
        }
        return price;
    }

    protected int getFirstHeaderIndex(List<List<Object>> sheet) {
        for (int y = 0; y < sheet.size(); y++) {
            if (isPrice(sheet, y)) {
                return --y;
            }
        }
        return 0;
    }

    protected boolean isPrice(List<List<Object>> sheet, int y) {
        return isPrice(sheet.get(y));
    }

    protected boolean isPrice(List<Object> line) {
        return getDouble(line, 1) != null
                && getDouble(line, 2) != null
                && getDouble(line, 3) != null;
    }

    private Double getDouble(List<Object> line, int x) {
        try {
            return Double.parseDouble(prepareDouble(line.get(x).toString()));
        } catch (Exception ex) {
            return null;
        }
    }

    private String prepareDouble(String value) {
        return value.trim().replaceAll(",", ".").replaceAll(" ", "");
    }

    private String translate(String value) {
        return toLatinTrans.transliterate(value);
    }

    private String prepareLink(String value) {
        return "/" + translate(
                (value.toLowerCase().trim().replaceAll("/", "_")
                .replaceAll(" ", "_")
                .replaceAll("\\(", "")
                .replaceAll("\\)", ""))
        );
    }
}
