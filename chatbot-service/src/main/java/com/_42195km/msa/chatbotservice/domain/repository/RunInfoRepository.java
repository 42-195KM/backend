package com._42195km.msa.chatbotservice.domain.repository;

import com._42195km.msa.chatbotservice.domain.entity.RunInfo;

public interface RunInfoRepository {
    RunInfo save(RunInfo runInfo);
}
