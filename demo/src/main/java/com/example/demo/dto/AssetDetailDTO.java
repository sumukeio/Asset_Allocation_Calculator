package com.example.demo.dto;

import com.example.demo.model.AssetDetail;
import java.math.BigDecimal;

/**
 * 用于前端提交或展示单个资产明细的数据传输对象
 */
public class AssetDetailDTO { // 资产明细DTO

    private Long id;
    private AssetDetail.AssetType assetType; // 使用枚举类型
    private String name;
    private BigDecimal amount;

    // ----------------------
    // 构造函数
    // ----------------------
    public AssetDetailDTO() {
    }

    public AssetDetailDTO(Long id, AssetDetail.AssetType assetType, String name, BigDecimal amount) {
        this.id = id;
        this.assetType = assetType;
        this.name = name;
        this.amount = amount;
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

    public AssetDetail.AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetDetail.AssetType assetType) {
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