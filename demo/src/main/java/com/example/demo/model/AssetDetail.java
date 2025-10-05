package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "asset_details")
public class AssetDetail {

    // ----------------------
    // 枚举定义：资产类型
    // ----------------------
    public enum AssetType {
        NASDAQ,        // 纳指持仓
        SP,            // 标普持仓
        CONSERVATIVE,  // 稳健投资
        CASH           // 现金
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    // ----------------------
    // 构造函数 (JPA 要求无参构造函数)
    // ----------------------
    public AssetDetail() {
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

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}