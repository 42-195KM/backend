package com._42195km.alertservice.presentation.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AlertMessageRequestDto {

    private String mediaId;
    private String message;

}
