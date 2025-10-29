package com.ord.core.crud.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncodedIdDto<TKey> {
    private String encodedId;
    private TKey id;
}
