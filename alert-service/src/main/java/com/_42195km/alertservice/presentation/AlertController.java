package com._42195km.alertservice.presentation;

import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;
import com._42195km.alertservice.presentation.dto.AlertMessageRequestDto;
import com._42195km.msa.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
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
