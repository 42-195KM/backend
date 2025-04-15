package com._42195km.alertservice.presentation;

import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.presentation.dto.AlertMessageRequestDto;
import com._42195km.msa.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/alerts")
public class AlertController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> postAlert(@RequestBody AlertMessageRequestDto alertMessageRequestDto) {
        messageService.sendMessage(alertMessageRequestDto.getMessage(), alertMessageRequestDto.getMediaId());
        return ResponseEntity.ok(ApiResponse.success(null,"메세지 전송 완료"));
    }




}
