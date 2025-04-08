package com_42195km.msa.runningrecordservice.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com_42195km.msa.runningrecordservice.application.service.RunningRecordService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/running-records")
@RequiredArgsConstructor
public class RunningRecordController {
	private final RunningRecordService runningRecordService;
}
