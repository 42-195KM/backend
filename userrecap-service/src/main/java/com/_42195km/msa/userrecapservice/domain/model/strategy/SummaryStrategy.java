package com._42195km.msa.userrecapservice.domain.model.strategy;

import java.util.List;

import com._42195km.msa.userrecapservice.domain.model.DataFormat;

public interface SummaryStrategy<T> {
	Double summary(DataFormat<T> df);

	Double calculatePercentile(List<T> distribution, T userValue);

}
