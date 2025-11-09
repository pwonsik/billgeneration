package wirelessbill.policy.monthlyfee.domain;

import wirelessbill.domain.monthlyfee.AdditionalBillingFactor;

public interface ProvidesAdditionalBillingDetails {
    AdditionalBillingFactor getAdditionalBillingFactors();
}
