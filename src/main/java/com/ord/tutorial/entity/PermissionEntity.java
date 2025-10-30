package com.ord.tutorial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {

    @Id
    @Column(name = "permission_name", unique = true, nullable = false)
    private String name; // ví dụ: "province.get-paged", "province.create"
}
