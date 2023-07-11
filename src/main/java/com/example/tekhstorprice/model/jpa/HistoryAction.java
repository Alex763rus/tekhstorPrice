package com.example.tekhstorprice.model.jpa;

import com.example.tekhstorprice.enums.ExportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "history_action")
public class HistoryAction {

    @Id
    @Column(name = "history_action_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyActionId;

    @Column(name = "chat_id_from", nullable = false)
    private Long chatIdFrom;

    @Column(name = "sheet_name")
    private String sheetName;

    @Column(name = "group_price")
    private String groupPrice;

    @Column(name = "model_price")
    private String modelPrice;

    @Column(name = "price_2_year")
    private Double price2year;

    @Column(name = "price_drop")
    private Double priceDrop;

    @Column(name = "price_opt")
    private Double priceOpt;

    @Column(name = "action_date")
    private Timestamp actionDate;

    @Column(name = "export_status")
    private ExportStatus exportStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryAction that = (HistoryAction) o;
        return Objects.equals(historyActionId, that.historyActionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(historyActionId);
    }
}
