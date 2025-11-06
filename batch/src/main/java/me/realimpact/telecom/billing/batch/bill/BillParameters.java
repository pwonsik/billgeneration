package me.realimpact.telecom.billing.batch.bill;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.realimpact.telecom.calculation.domain.BillingCalculationPeriod;
import me.realimpact.telecom.calculation.domain.BillingCalculationType;
import me.realimpact.telecom.calculation.domain.CalculationContext;

@Getter
@RequiredArgsConstructor
public class BillParameters {
    private final String invDt;
    private final String invOperCyclCd;
    private final String billOperCyclCd;
    private final String billOperNum;
    
//    private final BillingCalculationType billingCalculationType;
//    private final BillingCalculationPeriod billingCalculationPeriod;
    private final int threadCount;
    private final List<Long> contractIds;
    private final List<Long> acntIds;

//    public CalculationContext toCalculationContext() {
//        return new CalculationContext(
//        		invDt,
//        		invOperCyclCd,
//        		billOperCyclCd,
//        		billOperNum
//        );
//    }
}
