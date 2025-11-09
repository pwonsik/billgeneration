package batch.common.pipeline.steps;

import java.util.List;

import batch.common.pipeline.CalculationTargetBuilder;
import batch.common.pipeline.DataLoadingStep;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.discount.ContractDiscounts;
import wirelessbill.domain.discount.Discount;
import wirelessbill.port.out.ContractDiscountQueryPort;

/**
 * Discount 데이터를 로딩하는 파이프라인 단계
 */
public class LoadDiscountsStep implements DataLoadingStep<CalculationTargetBuilder> {
    
    private final ContractDiscountQueryPort discountQueryPort;
    
    public LoadDiscountsStep(ContractDiscountQueryPort discountQueryPort) {
        this.discountQueryPort = discountQueryPort;
    }
    
    @Override
    public void execute(CalculationTargetBuilder builder, Long contractId, CalculationContext context) {
        List<ContractDiscounts> contractDiscountsList = discountQueryPort.findContractDiscounts(
            List.of(contractId), 
            context.billingStartDate(), 
            context.billingEndDate()
        );
        
        List<Discount> discounts = contractDiscountsList.isEmpty() 
            ? List.of() 
            : contractDiscountsList.get(0).discounts();
        
        builder.discounts(discounts);
    }
}
