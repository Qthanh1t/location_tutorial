package com.ord.tutorial.dto.user;

import com.ord.core.crud.dto.EncodedIdDto;
import com.ord.tutorial.entity.RoleEntity;
import com.ord.tutorial.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends EncodedIdDto<Long> {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Boolean isActive;
    private LocalDateTime createdDate;
    private List<RoleEntity> roles;
}
