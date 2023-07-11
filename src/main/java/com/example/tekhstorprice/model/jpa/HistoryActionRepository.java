package com.example.tekhstorprice.model.jpa;

import com.example.tekhstorprice.enums.ExportStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryActionRepository extends CrudRepository<HistoryAction, Long> {

    public List<HistoryAction> findByExportStatus(ExportStatus exportStatus);
}
