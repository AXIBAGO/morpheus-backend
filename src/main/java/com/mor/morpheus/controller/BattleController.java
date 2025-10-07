package com.mor.morpheus.controller;

import com.mor.morpheus.dto.BattleEventDto;
import com.mor.morpheus.entity.BattleEvent;
import com.mor.morpheus.repository.BattleEventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.Instant;
import java.util.ArrayList;


/**
 * 战斗事件控制器
 * 负责：
 *  - 接收插件批量上传的战斗数据
 *  - 聚合并返回实时伤害榜单
 */
@RestController
@RequestMapping("/events")
public class BattleController {

    private final BattleEventRepository repo;

    public BattleController(BattleEventRepository repo) {
        this.repo = repo;
    }

    /**
     * 批量上传战斗事件
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> postBatch(@RequestBody List<BattleEventDto> events) {
        if (events == null || events.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "empty list"));
        }

        int saved = 0;
        List<BattleEvent> newEvents = new ArrayList<>();

        for (BattleEventDto dto : events) {
            long ts = dto.ts() != null ? dto.ts() : Instant.now().toEpochMilli();

            // ✅ 检查是否重复（playerId + ts + seq 唯一）
            boolean exists = repo.existsByPlayerIdAndTsAndSeq(dto.playerId(), ts, dto.seq());
            if (!exists) {
                newEvents.add(new BattleEvent(null, dto.playerId(), ts, dto.damage(), dto.seq()));
                saved++;
            }
        }

        // 统一保存
        if (!newEvents.isEmpty()) {
            repo.saveAll(newEvents);
        }

        return ResponseEntity.ok(Map.of(
                "received", events.size(),
                "saved", saved,
                "duplicates", events.size() - saved
        ));
    }


    /**
     * 获取最近时间窗口内的前10名伤害榜
     */
    @GetMapping("/rankings")
    public Map<String, Object> getRankings(
            @RequestParam(defaultValue = "60000") Long windowMs
    ) {
        long now = System.currentTimeMillis();
        long since = now - windowMs;

        var list = repo.findByTsGreaterThan(since);

        // 聚合 + 排序 + 截取Top10
        var rankings = list.stream()
                .collect(Collectors.groupingBy(
                        BattleEvent::getPlayerId,
                        Collectors.summingInt(BattleEvent::getDamage)))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("playerId", e.getKey());
                    m.put("totalDamage", e.getValue());

                    // 找出该玩家最近一次事件的时间
                    long lastTs = list.stream()
                            .filter(ev -> ev.getPlayerId().equals(e.getKey()))
                            .mapToLong(BattleEvent::getTs)
                            .max()
                            .orElse(0L);
                    m.put("lastTimestamp", lastTs);
                    return m;
                })
                .toList();

        // ✅ 最外层带当前查询时间戳
        return Map.of(
                "timestamp", now,
                "rankings", rankings
        );
    }
}
