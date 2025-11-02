package com.ord.tutorial.api;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.repository.spec.SpecificationBuilder;
import com.ord.core.crud.service.CrudAppService;
import com.ord.core.util.StringUtils;
import com.ord.tutorial.dto.user.UserCreateDto;
import com.ord.tutorial.dto.user.UserDto;
import com.ord.tutorial.dto.user.UserPageRequest;
import com.ord.tutorial.dto.user.UserUpdateDto;
import com.ord.tutorial.entity.RoleEntity;
import com.ord.tutorial.entity.User;
import com.ord.tutorial.repository.RoleRepository;
import com.ord.tutorial.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserApiResource extends CrudAppService<
        User,
        Long,
        UserDto,
        UserPageRequest,
        UserDto,
        UserCreateDto,
        UserUpdateDto> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    protected Specification<User> buildSpecificationForPaging(UserPageRequest pageRequest) {
        return SpecificationBuilder.<User>builder()
                .withLikeFts(pageRequest.getFts(), "username", "email", "fullName")
                .withEqIfNotNull("enabled", pageRequest.getIsActive())
                .withRange("createdDate", pageRequest.getCreatedDate())
                .build();
    }

    @Override
    protected void validationBeforeCreate(UserCreateDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throwBusiness(getMessage("user.error.usernameExists"));
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throwBusiness(getMessage("user.error.emailExists"));
        }
        List<RoleEntity> roles = roleRepository.findByNameIn(userDto.getRoles());
        if (roles.isEmpty()) {
            throwBusiness("Không tìm thấy role nào hợp lệ trong " + roles);
        }
    }

    @Override
    protected User convertCreateInputToEntity(UserCreateDto userCreateDto) {
        var user = super.convertCreateInputToEntity(userCreateDto);
        if (userCreateDto.getRoles() != null && !userCreateDto.getRoles().isEmpty()) {
            List<RoleEntity> roles = roleRepository.findByNameIn(userCreateDto.getRoles());
            user.setRoles(roles);
        }
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setEnabled(Boolean.TRUE);
        user.setCreatedDate(LocalDateTime.now());
        return user;
    }

    @Override
    protected void validationBeforeUpdate(UserUpdateDto userDto, User entityToUpdate) {
        if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), entityToUpdate.getId())) {
            throwBusiness(getMessage("user.error.emailExists"));
        }
    }

    @Override
    protected void applyUpdateToEntity(UserUpdateDto userUpdateDto, User entityToUpdate) {
        if (!StringUtils.isNullOrBlank(userUpdateDto.getPassword())) {
            entityToUpdate.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        } else {
            userUpdateDto.setPassword(entityToUpdate.getPassword());
        }
        super.applyUpdateToEntity(userUpdateDto, entityToUpdate);
    }

    @Override
    protected OrdEntityRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected String getEntityName() {
        return "user";
    }


}
