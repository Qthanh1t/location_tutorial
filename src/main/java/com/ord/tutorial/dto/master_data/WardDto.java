package com.ord.tutorial.dto.master_data;

import com.ord.core.crud.dto.EncodedIdDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WardDto extends EncodedIdDto<Integer> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @NotBlank
    @Size(max = 20)
    private String code;
    @NotBlank
    @Size(max = 200)
    private String name;
    @NotBlank
    @Size(max = 20)
    private String provinceCode;
    private String provinceName;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    public WardDto(
            Integer id, String code, String name, String provinceCode, String provinceName,
            String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {

        this.setId(id);

        this.code = code;
        this.name = name;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }
}
