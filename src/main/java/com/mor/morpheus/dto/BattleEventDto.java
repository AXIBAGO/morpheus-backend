package com.mor.morpheus.dto;

/**
 * 用于插件或外部系统上传的战斗事件 DTO
 * （DTO = Data Transfer Object）
 */
public record BattleEventDto(
        String playerId,
        Long ts,      // ✅ 改成大写 Long
        Integer damage,
        Integer seq
) {}
