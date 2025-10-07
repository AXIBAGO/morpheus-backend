package com.mor.morpheus.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Table;

/**
 * 数据库存储实体类
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(
        name = "battle_event",
        uniqueConstraints = @UniqueConstraint(columnNames = {"playerId","ts","seq"})
)
public class BattleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerId;
    private Long ts;
    private Integer damage;
    private Integer seq;
}
