package bill.policy.monthlyfee.domain;

import bill.domain.monthlyfee.AdditionalBillingFactor;

public interface ProvidesAdditionalBillingDetails {
    AdditionalBillingFactor getAdditionalBillingFactors();
}
