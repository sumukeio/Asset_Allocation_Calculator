package com.example.demo.controller;

import com.example.demo.dto.AssetDetailDTO;
import com.example.demo.dto.HistoryRecordDTO;
import com.example.demo.dto.RecommendationDTO;
import com.example.demo.service.AssetService;
import com.example.demo.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 资产控制器，负责处理前端在“开始配置”页面上的所有操作。


@RestController
@RequestMapping("/api") // 所有接口都以 /api 开头
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private CalculationService calculationService;

    // ==========================================================
    // 资产明细 (CRUD) 接口
    // ==========================================================

    /**
     * POST /api/assets : 添加新的资产明细
     * @param dto 前端传入的资产 DTO (assetType, name, amount)
     * @return 保存后的资产 DTO
     */
    @PostMapping("/assets")
    public ResponseEntity<AssetDetailDTO> addAsset(@RequestBody AssetDetailDTO dto) {
        // 使用 AssetService 保存资产
        AssetDetailDTO saved = assetService.saveAsset(dto);
        // 返回 HTTP 201 Created 状态码
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * GET /api/assets : 获取当前所有资产明细
     * @return 所有资产明细 DTO 列表
     */
    @GetMapping("/assets")
    public ResponseEntity<List<AssetDetailDTO>> getAllAssets() {
        List<AssetDetailDTO> assets = assetService.findAllAssets();
        return ResponseEntity.ok(assets);
    }


    // ==========================================================
    // 推荐配置计算接口
    // ==========================================================

    /**
     * GET /api/calculate/recommendation : 计算并返回推荐配置
     * @return 包含当前持仓和目标配置的 DTO
     */
    @GetMapping("/calculate/recommendation")
    public ResponseEntity<?> getRecommendation() {
        try {
            RecommendationDTO recommendation = calculationService.getRecommendation();
            return ResponseEntity.ok(recommendation);
        } catch (IllegalStateException e) {
            // 如果 CalculationService 抛出异常 (例如：总资产为零)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }





    // ==========================================================
    // 历史记录接口
    // ==========================================================

    /**
     * POST /api/records : 保存当前资产配置为历史快照
     * @return 保存后的历史记录 DTO
     */
    @PostMapping("/records")
    public ResponseEntity<?> saveHistoryRecord() {
        try {
            HistoryRecordDTO savedRecord = assetService.saveCurrentAssetsAsRecord();
            return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * GET /api/records : 获取所有历史记录
     * @return 历史记录 DTO 列表
     */
    @GetMapping("/records")
    public ResponseEntity<List<HistoryRecordDTO>> getAllHistoryRecords() {
        List<HistoryRecordDTO> records = assetService.findAllHistoryRecords();
        return ResponseEntity.ok(records);
    }
}