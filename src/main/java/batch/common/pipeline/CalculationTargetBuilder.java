package batch.common.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bill.domain.CalculationTarget;
import bill.domain.discount.Discount;
import bill.domain.monthlyfee.MonthlyChargeDomain;
import bill.domain.onetimecharge.OneTimeChargeDomain;

/**
 * CalculationTarget을 단계적으로 구성하는 빌더
 */
public class CalculationTargetBuilder {
    
    private Long contractId;
    private final Map<Class<? extends MonthlyChargeDomain>, List<? extends MonthlyChargeDomain>> monthlyChargeData = new HashMap<>();
    private final Map<Class<? extends OneTimeChargeDomain>, List<? extends OneTimeChargeDomain>> oneTimeChargeData = new HashMap<>();
    private List<Discount> discounts = new ArrayList<>();
    
    public CalculationTargetBuilder contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }
    
    public <T extends MonthlyChargeDomain> CalculationTargetBuilder addMonthlyChargeData(Class<T> type, List<T> data) {
        this.monthlyChargeData.put(type, data);
        return this;
    }
    
    public <T extends OneTimeChargeDomain> CalculationTargetBuilder addOneTimeChargeData(Class<T> type, List<T> data) {
        this.oneTimeChargeData.put(type, data);
        return this;
    }
    
    public CalculationTargetBuilder discounts(List<Discount> discounts) {
        this.discounts = discounts;
        return this;
    }
    
    public CalculationTarget build() {
        return new CalculationTarget(contractId, monthlyChargeData, oneTimeChargeData, discounts);
    }
}
