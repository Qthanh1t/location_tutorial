package com.ord.tutorial.dto.master_data;

import com.ord.core.crud.dto.PagedResultRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WardPagedInput extends PagedResultRequestDto {
    private String provinceCode;
}
