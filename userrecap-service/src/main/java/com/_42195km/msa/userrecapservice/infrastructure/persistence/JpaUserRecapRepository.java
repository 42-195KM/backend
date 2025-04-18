package com._42195km.msa.userrecapservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.userrecapservice.domain.model.Recap;
import com._42195km.msa.userrecapservice.domain.repository.UserRecapRepository;

public interface JpaUserRecapRepository extends JpaRepository<Recap, UUID>, UserRecapRepository {

}
