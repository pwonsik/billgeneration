package wirelessbill.domain;

import common.domain.BillGenerationType;

public record BillContext(
		String invDt,
		BillGenerationType billGenerationType) {
}
