package com.example.demo.repository;

import com.example.demo.model.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
// 负责记录数据的持久化操作

@Repository
public interface HistoryRecordRepository extends JpaRepository<HistoryRecord, Long> {

    // 自定义方法：获取所有记录，并按日期倒序排列（最新记录在前）
    List<HistoryRecord> findAllByOrderByRecordDateDesc();
}