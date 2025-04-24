package com._42195km.msa.userrecapservice.domain.repository;

import com._42195km.msa.userrecapservice.domain.model.Recap;

public interface UserRecapRepository {
	Recap save(Recap userRecap);
}
