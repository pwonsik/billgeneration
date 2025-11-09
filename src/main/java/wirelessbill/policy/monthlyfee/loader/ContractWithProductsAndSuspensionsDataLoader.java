package bill.policy.monthlyfee.loader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import bill.application.monthlyfee.MonthlyFeeDataLoader;
import bill.application.service.BillingPeriodService;
import bill.domain.CalculationContext;
import bill.domain.monthlyfee.DefaultPeriod;
import bill.domain.monthlyfee.MonthlyChargeDomain;
import bill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;
import bill.policy.monthlyfee.port.ProductQueryPortResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ContractWithProductsAndSuspensions 데이터 로딩 전담 클래스
 * 단일 책임: 계약 상품 정보 및 정지 정보 데이터 로딩
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContractWithProductsAndSuspensionsDataLoader implements MonthlyFeeDataLoader<ContractWithProductsAndSuspensions> {

    private final ProductQueryPortResolver productQueryPortResolver;
    private final BillingPeriodService billingPeriodService;

    @Override
    public Class<ContractWithProductsAndSuspensions> getDomainType() {
        return ContractWithProductsAndSuspensions.class;
    }

    @Override
    public Map<Long, List<? extends MonthlyChargeDomain>> read(List<Long> contractIds, CalculationContext ctx) {
        log.debug("Loading ContractWithProductsAndSuspensions data for {} contracts", contractIds.size());

        DefaultPeriod billingPeriod = billingPeriodService.createBillingPeriod(ctx);

        Map<Long, List<ContractWithProductsAndSuspensions>> specificData = productQueryPortResolver
                .getProductQueryPort(ctx.billingCalculationType())
                .findContractsAndProductInventoriesByContractIds(
                    contractIds, billingPeriod.getStartDate(), billingPeriod.getEndDate()
            ).stream()
            .collect(Collectors.groupingBy(ContractWithProductsAndSuspensions::getContractId));

        log.debug("Loaded ContractWithProductsAndSuspensions data for {} contracts", specificData.size());

        return specificData.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}