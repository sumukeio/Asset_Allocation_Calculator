package com.example.demo.service;

import com.example.demo.dto.AssetDetailDTO;
import com.example.demo.dto.HistoryRecordDTO;
import com.example.demo.model.AssetDetail;
import com.example.demo.model.AssetDetail.AssetType;
import com.example.demo.model.HistoryRecord;
import com.example.demo.repository.AssetDetailRepository;
import com.example.demo.repository.HistoryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class AssetService { // 资产服务，负责把DTO转换为Entity并保存到数据库，以及将Entity转换为DTO并返回给前端

    @Autowired
    private AssetDetailRepository assetDetailRepository;

    @Autowired
    private HistoryRecordRepository historyRecordRepository;
    /**
     * 将 DTO 转换为 Entity 并保存到数据库
     * 
     * @param dto 前端传入的资产明细 DTO
     * @return 保存后的 DTO
     */
    public AssetDetailDTO saveAsset(AssetDetailDTO dto) {
        // 1. DTO 转换为 Entity
        AssetDetail asset = new AssetDetail();
        asset.setId(null);
        asset.setAssetType(dto.getAssetType());
        asset.setName(dto.getName());
        asset.setAmount(dto.getAmount());

        // 2. 保存到数据库
        AssetDetail savedAsset = assetDetailRepository.save(asset);

        // 3. Entity 转换为 DTO 返回
        return mapToDTO(savedAsset);
    }

    /**
     * 获取所有资产明细
     * 
     * @return 所有资产明细 DTO 列表
     */
    public List<AssetDetailDTO> findAllAssets() {
        // 查找所有 Entity 并转换为 DTO 列表
        return assetDetailRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取某一资产类型的所有资产总额
     * 
     * @param type 资产类型
     * @return 该类型资产总额
     */
    public List<AssetDetail> findAssetsByType(AssetDetail.AssetType type) {
        return assetDetailRepository.findByAssetType(type);
    }

    // --- 历史记录相关方法 ---

    /**
     * 根据当前资产明细，生成快照并保存为历史记录。
     * 
     * @return HistoryRecordDTO
     */
    public HistoryRecordDTO saveCurrentAssetsAsRecord() {
        // 1. 获取当前所有资产明细
        List<AssetDetailDTO> allAssets = findAllAssets();

        if (allAssets.isEmpty()) {
            throw new IllegalStateException("当前没有资产明细，无法保存历史记录。");
        }

        // 2. 聚合各类资产总额
        BigDecimal nasdaqTotal = sumAmount(allAssets, AssetType.NASDAQ);
        BigDecimal spTotal = sumAmount(allAssets, AssetType.SP);
        BigDecimal conservativeTotal = sumAmount(allAssets, AssetType.CONSERVATIVE);
        BigDecimal cashTotal = sumAmount(allAssets, AssetType.CASH);

        BigDecimal grandTotal = nasdaqTotal.add(spTotal).add(conservativeTotal).add(cashTotal);

        // 3. 构建 HistoryRecord 实体
        HistoryRecord record = new HistoryRecord();
        record.setRecordDate(LocalDateTime.now());
        record.setGrandTotal(grandTotal);
        record.setNasdaqTotal(nasdaqTotal);
        record.setSpTotal(spTotal);
        record.setConservativeTotal(conservativeTotal);
        record.setCashTotal(cashTotal);

        // 4. 保存到数据库
        HistoryRecord savedRecord = historyRecordRepository.save(record);

        // 5. 转换为 DTO 返回
        return mapToDTO(savedRecord);
    }

    /**
     * 获取所有历史记录（按日期倒序）
     * 
     * @return 历史记录 DTO 列表
     */
    public List<HistoryRecordDTO> findAllHistoryRecords() {
        return historyRecordRepository.findAllByOrderByRecordDateDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 辅助方法：计算某一资产类型的所有金额总和
     */
    private BigDecimal sumAmount(List<AssetDetailDTO> assets, AssetType type) {
        return assets.stream()
                .filter(asset -> asset.getAssetType() == type)
                .map(AssetDetailDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 辅助方法：将 HistoryRecord Entity 映射为 DTO
     */
    private HistoryRecordDTO mapToDTO(HistoryRecord record) {
        HistoryRecordDTO dto = new HistoryRecordDTO();
        dto.setId(record.getId());
        dto.setRecordDate(record.getRecordDate());
        dto.setGrandTotal(record.getGrandTotal());
        dto.setNasdaqTotal(record.getNasdaqTotal());
        dto.setSpTotal(record.getSpTotal());
        dto.setConservativeTotal(record.getConservativeTotal());
        dto.setCashTotal(record.getCashTotal());
        return dto;
    }

    /**
     * 辅助方法：将 Entity 映射为 DTO
     */
    private AssetDetailDTO mapToDTO(AssetDetail asset) {
        return new AssetDetailDTO(
                asset.getId(),
                asset.getAssetType(),
                asset.getName(),
                asset.getAmount());
    }
}
