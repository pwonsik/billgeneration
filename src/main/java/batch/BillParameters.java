package batch;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wirelessbill.domain.BillingCalculationPeriod;
import wirelessbill.domain.BillingCalculationType;
import wirelessbill.domain.CalculationContext;

@Getter
@RequiredArgsConstructor
public class BillParameters {
    private final LocalDate billingStartDate;
    private final LocalDate billingEndDate;
    private final BillingCalculationType billingCalculationType;
    private final BillingCalculationPeriod billingCalculationPeriod;
    private final int threadCount;
    private final List<Long> contractIds;

    public CalculationContext toCalculationContext() {
        return new CalculationContext(
                billingStartDate,
                billingEndDate,
                billingCalculationType,
                billingCalculationPeriod
        );
    }
}