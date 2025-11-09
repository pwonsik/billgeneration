package bill.domain;

import java.time.LocalDate;

public record CalculationContext(
        LocalDate billingStartDate,
        LocalDate billingEndDate,
        BillingCalculationType billingCalculationType,
        BillingCalculationPeriod billingCalculationPeriod) {
}
