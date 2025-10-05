package com.example.demo.repository;

import com.example.demo.model.AssetDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetDetailRepository extends JpaRepository<AssetDetail, Long> {
    
    /**
     * 根据资产类型查找所有资产明细
     * Spring Data JPA 会自动实现此方法
     * @param assetType 资产类型 (NASDAQ, SP, etc.)
     * @return 资产明细列表
     */
    List<AssetDetail> findByAssetType(AssetDetail.AssetType assetType);
    
    /**
     * Spring Data JPA 默认提供了 findAll(), save(), findById() 等方法
     */
}