package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// 这个实体用于存储用户点击“保存”时的配置快照，将映射到 PostgreSQL 中的 history_records 表。

@Entity
@Table(name = "history_records")
public class HistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 记录时间
    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    // 总资产
    @Column(name = "grand_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal grandTotal;

    // 各项总额快照
    @Column(name = "nasdaq_total", precision = 19, scale = 2)
    private BigDecimal nasdaqTotal;

    @Column(name = "sp_total", precision = 19, scale = 2)
    private BigDecimal spTotal;

    // 稳健投资总额
    @Column(name = "conservative_total", precision = 19, scale = 2)
    private BigDecimal conservativeTotal;

    @Column(name = "cash_total", precision = 19, scale = 2)
    private BigDecimal cashTotal;

    // ----------------------
    // 构造函数
    // ----------------------
    public HistoryRecord() {
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