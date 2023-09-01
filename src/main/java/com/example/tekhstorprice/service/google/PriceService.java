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
import java.util.List;
import java.util.Map;

import static org.example.tgcommons.constant.Constant.TextConstants.NEW_LINE;
import static org.example.tgcommons.constant.Constant.TextConstants.STAR;

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
        priceInfo.append(STAR).append("Справочники успешно обновлены!").append(STAR).append(NEW_LINE)
                .append(" - загружено файлов: " + price.size()).append(NEW_LINE)
                .append(" - загружено товаров: " + price.values().stream().mapToInt(e -> e.size()).sum());
        return priceInfo.toString();
    }

    public Map<String, Price> getPrice(SheetName sheetName) {
        return price.get(sheetName);
    }

    public void updatePrice() {
        price = new HashMap<>();
        for (SheetName sheetName : SheetName.values()) {
            price.put(sheetName, createPrice(googleSheetsService.getSheetData(sheetName)));
        }
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
                        .name(prepareText(line.get(0).toString()))
                        .priceCommand(prepareLink(line.get(0).toString()))
                        .price2year(getDouble(line, 1))
                        .priceDrop(getDouble(line, 2))
                        .priceOpt(getDouble(line, 3))
                        .order(positionInGroup)
                        .build();
                ++positionInGroup;
                price.put(prepareLink(pricePojo.getName()), pricePojo);
            } else {
                val groupName = line.get(0).toString();
                priceGroup = PriceGroup.builder()
                        .groupName(prepareText(groupName))
                        .groupCommand(prepareLink(groupName))
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
        val prepareLink = value.toLowerCase().trim()
                .replaceAll(":", "_")
                .replaceAll("–", "_")
                .replaceAll("ё", "е")
                .replaceAll("&", "_")
                .replaceAll("/", "_")
                .replaceAll("\"", "_")
                .replaceAll("”", "_")
                .replaceAll("\\+", "_")
                .replaceAll(" ", "_")
                .replaceAll("-", "_")
                .replaceAll("\\.", "_")
                .replaceAll("'", "_")
                .replaceAll(",", "")
                .replaceAll("»", "")
                .replaceAll("«", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "");
        val translateLink = translate(prepareLink);
        val link = "/" + translateLink
                .replaceAll("ʹ", "");
        val linkLength = link.length() > 64 ? 64 : link.length();
        return link.substring(0, linkLength);
    }

    private String prepareText(String value) {
        return value.replaceAll("/", "")
                .replaceAll("_", "")
                .replaceAll("»", "")
                .replaceAll("«", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "");
    }
}
