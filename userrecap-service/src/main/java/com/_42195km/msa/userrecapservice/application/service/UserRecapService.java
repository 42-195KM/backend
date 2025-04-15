package com._42195km.msa.userrecapservice.application.service;

import java.util.List;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.domain.model.strategy.SummaryType;

public interface UserRecapService {
	void createUserRecap(SummaryType summaryType, List<GetRunningRecordAppResponseDto>data);
}
