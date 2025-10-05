package com.example.demo.dto;

import java.math.BigDecimal;

/**
 * 用于返回资产配置推荐结果的数据传输对象
 * 包含当前持仓总额，以及计算出的各资产的目标金额和比例。
 */
public class RecommendationDTO { // 推荐配置DTO

    // 总资产
    private BigDecimal grandTotal;

    // 当前持仓金额（用于对比）
    private BigDecimal nasdaqCurrent;
    private BigDecimal spCurrent;
    private BigDecimal conservativeCurrent;
    private BigDecimal cashCurrent;

    // 目标推荐金额
    private BigDecimal nasdaqTarget;
    private BigDecimal spTarget;
    private BigDecimal cashTarget; // 注意：稳健投资的目标金额在推荐中通常为0或忽略

    // 目标推荐比例 (格式化为字符串，如 "57.75%")
    private String nasdaqTargetRatio;
    private String spTargetRatio;
    private String cashTargetRatio;

    // ----------------------
    // 构造函数 (省略，通常通过 Builder 或 Setters 填充)
    // ----------------------
    public RecommendationDTO() {
    }

    // ----------------------
    // Getter 和 Setter 方法
    // ----------------------

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getNasdaqCurrent() {
        return nasdaqCurrent;
    }

    public void setNasdaqCurrent(BigDecimal nasdaqCurrent) {
        this.nasdaqCurrent = nasdaqCurrent;
    }

    public BigDecimal getSpCurrent() {
        return spCurrent;
    }

    public void setSpCurrent(BigDecimal spCurrent) {
        this.spCurrent = spCurrent;
    }

    public BigDecimal getConservativeCurrent() {
        return conservativeCurrent;
    }

    public void setConservativeCurrent(BigDecimal conservativeCurrent) {
        this.conservativeCurrent = conservativeCurrent;
    }

    public BigDecimal getCashCurrent() {
        return cashCurrent;
    }

    public void setCashCurrent(BigDecimal cashCurrent) {
        this.cashCurrent = cashCurrent;
    }

    public BigDecimal getNasdaqTarget() {
        return nasdaqTarget;
    }

    public void setNasdaqTarget(BigDecimal nasdaqTarget) {
        this.nasdaqTarget = nasdaqTarget;
    }

    public BigDecimal getSpTarget() {
        return spTarget;
    }

    public void setSpTarget(BigDecimal spTarget) {
        this.spTarget = spTarget;
    }

    public BigDecimal getCashTarget() {
        return cashTarget;
    }

    public void setCashTarget(BigDecimal cashTarget) {
        this.cashTarget = cashTarget;
    }

    public String getNasdaqTargetRatio() {
        return nasdaqTargetRatio;
    }

    public void setNasdaqTargetRatio(String nasdaqTargetRatio) {
        this.nasdaqTargetRatio = nasdaqTargetRatio;
    }

    public String getSpTargetRatio() {
        return spTargetRatio;
    }

    public void setSpTargetRatio(String spTargetRatio) {
        this.spTargetRatio = spTargetRatio;
    }

    public String getCashTargetRatio() {
        return cashTargetRatio;
    }

    public void setCashTargetRatio(String cashTargetRatio) {
        this.cashTargetRatio = cashTargetRatio;
    }
}