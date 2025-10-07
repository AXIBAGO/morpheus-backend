package com.mor.morpheus.entity;

import jakarta.persistence.*;  // JPA 的注解包
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity                // 表示这是数据库表的实体
@Table(name = "users") // 数据库表名
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
}
