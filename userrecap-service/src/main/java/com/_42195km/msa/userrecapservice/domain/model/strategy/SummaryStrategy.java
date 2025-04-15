package com._42195km.msa.userrecapservice.domain.model.strategy;

import java.util.List;
import java.util.function.ToDoubleFunction;

public interface SummaryStrategy {
	Double summary(List<Double> data);

	Double calculatePercentile(List<Double> data, Double userValue);

}
