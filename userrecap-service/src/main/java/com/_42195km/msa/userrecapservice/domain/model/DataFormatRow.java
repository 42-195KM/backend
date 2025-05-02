package com._42195km.msa.userrecapservice.domain.model;

import java.util.Objects;
import java.util.UUID;

import com._42195km.msa.userrecapservice.domain.model.strategy.SummaryType;

public record DataFormatRow<T>(
	UUID userId,
	T value,
	SummaryType summaryType
) {
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DataFormatRow<?> that))
			return false;
		return Objects.equals(value, that.value) && Objects.equals(userId, that.userId)
			&& summaryType == that.summaryType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, value, summaryType);
	}
}
