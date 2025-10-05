package com.example.demo.service;

import com.example.demo.dto.RecommendationDTO;
import com.example.demo.model.AssetDetail;
import com.example.demo.model.AssetDetail.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CalculationService {

    // 引入 AssetService 来获取当前资产数据
    @Autowired
    private AssetService assetService;

    // ----------------------------------------------------
    // 固定的配置比例 (定义为常量)
    // ----------------------------------------------------
    private static final BigDecimal TOTAL_RISK_RATIO = new BigDecimal("0.75");
    private static final BigDecimal CASH_RATIO = new BigDecimal("0.25");
    private static final BigDecimal NASDAQ_IN_RISK = new BigDecimal("0.77");
    private static final BigDecimal SP_IN_RISK = new BigDecimal("0.23");
    
    // 计算精度
    private static final int SCALE = 2; 

    /**
     * 执行核心计算，返回推荐配置 DTO
     * @return RecommendationDTO
     */
    public RecommendationDTO getRecommendation() {
        // 1. 获取当前所有资产明细 (Entity)
        List<AssetDetail> allAssets = assetService.findAllAssets().stream()
                .map(this::mapToEntity) // 将 DTO 列表转换为 Entity 列表，方便后续聚合
                .toList();

        // 2. 统计当前各资产类型总额和总资产
        BigDecimal nasdaqCurrent = sumAmountByType(allAssets, AssetType.NASDAQ);
        BigDecimal spCurrent = sumAmountByType(allAssets, AssetType.SP);
        BigDecimal conservativeCurrent = sumAmountByType(allAssets, AssetType.CONSERVATIVE);
        BigDecimal cashCurrent = sumAmountByType(allAssets, AssetType.CASH);
        
        // 总资产
        BigDecimal grandTotal = nasdaqCurrent
                .add(spCurrent)
                .add(conservativeCurrent)
                .add(cashCurrent);

        if (grandTotal.compareTo(BigDecimal.ZERO) == 0) {
            // 如果总资产为零，则不进行后续计算
            throw new IllegalStateException("当前资产总额为零，无法计算推荐配置。");
        }
        
        // 3. 计算目标金额
        // 总风险金额 = 总资产 * 75%
        BigDecimal totalRiskTarget = grandTotal.multiply(TOTAL_RISK_RATIO).setScale(SCALE, RoundingMode.HALF_UP);
        
        // 纳指目标 = 总风险金额 * 77%
        BigDecimal nasdaqTarget = totalRiskTarget.multiply(NASDAQ_IN_RISK).setScale(SCALE, RoundingMode.HALF_UP);
        
        // 标普目标 = 总风险金额 * 23%
        BigDecimal spTarget = totalRiskTarget.multiply(SP_IN_RISK).setScale(SCALE, RoundingMode.HALF_UP);
        
        // 现金目标 = 总资产 * 25%
        BigDecimal cashTarget = grandTotal.multiply(CASH_RATIO).setScale(SCALE, RoundingMode.HALF_UP);
        
        // 4. 构建返回 DTO
        RecommendationDTO dto = new RecommendationDTO();
        dto.setGrandTotal(grandTotal);

        // 当前金额
        dto.setNasdaqCurrent(nasdaqCurrent);
        dto.setSpCurrent(spCurrent);
        dto.setConservativeCurrent(conservativeCurrent);
        dto.setCashCurrent(cashCurrent);

        // 目标金额
        dto.setNasdaqTarget(nasdaqTarget);
        dto.setSpTarget(spTarget);
        dto.setCashTarget(cashTarget);
        
        // 目标比例 (格式化为字符串，方便前端直接展示)
        dto.setNasdaqTargetRatio(TOTAL_RISK_RATIO.multiply(NASDAQ_IN_RISK).multiply(new BigDecimal("100")).setScale(SCALE, RoundingMode.HALF_UP) + "%"); // 57.75%
        dto.setSpTargetRatio(TOTAL_RISK_RATIO.multiply(SP_IN_RISK).multiply(new BigDecimal("100")).setScale(SCALE, RoundingMode.HALF_UP) + "%");       // 17.25%
        dto.setCashTargetRatio(CASH_RATIO.multiply(new BigDecimal("100")).setScale(SCALE, RoundingMode.HALF_UP) + "%");                                // 25.00%

        return dto;
    }

    /**
     * 辅助方法：计算某一资产类型的所有金额总和
     */
    private BigDecimal sumAmountByType(List<AssetDetail> assets, AssetType type) {
        return assets.stream()
                .filter(asset -> asset.getAssetType() == type)
                .map(AssetDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * 辅助方法：将 DTO 转换为 Entity (这里假设我们从 AssetService 拿到的已经是 DTO，需转回 Entity)
     * 注意：这个辅助方法是为了让 CalculationService 独立地处理数据聚合。
     * 实际在 AssetService 中已完成 Entity到DTO的转换。
     * 由于我们这里直接使用了 AssetService 拿到的 List<AssetDetailDTO>，
     * 理论上需要一个转换，但为了简化，我们假设 AssetService 能够直接返回 List<AssetDetail>
     * 或者我们在 AssetService 中增加一个方法，但现在先用这个简化方式。
     */
    private AssetDetail mapToEntity(com.example.demo.dto.AssetDetailDTO dto) {
        AssetDetail asset = new AssetDetail();
        asset.setId(dto.getId());
        asset.setAssetType(dto.getAssetType());
        asset.setName(dto.getName());
        asset.setAmount(dto.getAmount());
        return asset;
    }
}