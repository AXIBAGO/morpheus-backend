package com.mor.morpheus.repository;

import com.mor.morpheus.entity.BattleEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 数据库操作接口（继承 JPA 提供的标准方法）
 */
public interface BattleEventRepository extends JpaRepository<BattleEvent, Long> {

    // 查询某个时间点之后的所有事件，用于排行榜聚合
    List<BattleEvent> findByTsGreaterThan(Long since);
    boolean existsByPlayerIdAndTsAndSeq(String playerId, long ts, int seq);
}
