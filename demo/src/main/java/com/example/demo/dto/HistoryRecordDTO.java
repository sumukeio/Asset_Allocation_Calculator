package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 传输历史记录数据给前端

/**
 * 用于向前端展示历史配置快照的数据传输对象
 */
public class HistoryRecordDTO {

    private Long id;
    private LocalDateTime recordDate;
    private BigDecimal grandTotal;
    private BigDecimal nasdaqTotal;
    private BigDecimal spTotal;
    private BigDecimal conservativeTotal;
    private BigDecimal cashTotal;

    // ----------------------
    // 构造函数
    // ----------------------
    public HistoryRecordDTO() {
    }

    // ----------------------
    // Getter 和 Setter 方法
    // ----------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getNasdaqTotal() {
        return nasdaqTotal;
    }

    public void setNasdaqTotal(BigDecimal nasdaqTotal) {
        this.nasdaqTotal = nasdaqTotal;
    }

    public BigDecimal getSpTotal() {
        return spTotal;
    }

    public void setSpTotal(BigDecimal spTotal) {
        this.spTotal = spTotal;
    }

    public BigDecimal getConservativeTotal() {
        return conservativeTotal;
    }

    public void setConservativeTotal(BigDecimal conservativeTotal) {
        this.conservativeTotal = conservativeTotal;
    }

    public BigDecimal getCashTotal() {
        return cashTotal;
    }

    public void setCashTotal(BigDecimal cashTotal) {
        this.cashTotal = cashTotal;
    }
}