package com.ord.tutorial.entity;

import com.ord.core.crud.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "wards")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WardEntity extends AuditableEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String name;
    @Column(name = "province_code")
    private String provinceCode;
}
