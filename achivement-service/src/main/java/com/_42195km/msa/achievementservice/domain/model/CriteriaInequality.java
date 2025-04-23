package com._42195km.msa.achievementservice.domain.model;

public enum CriteriaInequality {
	LESS_THAN {
		@Override
		public boolean compare(double actualValue, double criteriaValue) {
			return actualValue < criteriaValue;
		}
	},
	LESS_THAN_OR_EQUAL_TO{
		@Override
		public boolean compare(double actualValue, double criteriaValue) {
			return actualValue <= criteriaValue;
		}
	},
	EQUAL{
		@Override
		public boolean compare(double actualValue, double criteriaValue) {
			return actualValue == criteriaValue;
		}
	},
	NOT_EQUAL{
		@Override
		public boolean compare(double actualValue, double criteriaValue) {
			return actualValue != criteriaValue;
		}
	},
	MORE_THAN_OR_EQUAL_TO{
		@Override
		public boolean compare(double actualValue, double criteriaValue) {
			return actualValue >= criteriaValue;
		}
	},
	MORE_THAN{
		@Override
		public boolean compare(double actualValue, double criteriaValue) {
			return actualValue > criteriaValue;
		}
	};

	public abstract boolean compare(double actualValue, double criteriaValue);
}
