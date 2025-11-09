package wirelessbill.policy.monthlyfee.domain;

import wirelessbill.domain.monthlyfee.CalculationMethod;
import wirelessbill.domain.monthlyfee.Pricing;

public interface MonthlyChargingPolicyFactory {
    Pricing getPolicy(CalculationMethod calculationMethod);
}
