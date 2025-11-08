package batch;

import java.time.LocalDate;
import java.util.List;

import bill.domain.BillingCalculationPeriod;
import bill.domain.BillingCalculationType;
import bill.domain.CalculationContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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