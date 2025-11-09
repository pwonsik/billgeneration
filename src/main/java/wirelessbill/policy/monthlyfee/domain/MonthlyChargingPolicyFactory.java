package bill.policy.monthlyfee.domain;

import bill.domain.monthlyfee.CalculationMethod;
import bill.domain.monthlyfee.Pricing;

public interface MonthlyChargingPolicyFactory {
    Pricing getPolicy(CalculationMethod calculationMethod);
}
