package wirelessbill.policy.monthlyfee.calculator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wirelessbill.application.monthlyfee.MonthlyFeeCalculator;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationResult;
import wirelessbill.policy.monthlyfee.domain.ContractWithProductsAndSuspensions;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 기본 정책 기반 월정액 계산기
 * 단일 책임: 월정액 계산 로직만 담당
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(10)
public class BasicPolicyMonthlyFeeCalculator implements MonthlyFeeCalculator<ContractWithProductsAndSuspensions> {

    // MonthlyFeeCalculator 인터페이스 구현
    @Override
    public Class<ContractWithProductsAndSuspensions> getDomainType() {
        return ContractWithProductsAndSuspensions.class;
    }

    @Override
    public List<CalculationResult<ContractWithProductsAndSuspensions>> process(
        CalculationContext ctx,
        ContractWithProductsAndSuspensions contractWithProductInventoriesAndSuspensions
    ) {
        var result = contractWithProductInventoriesAndSuspensions.buildProratedPeriods().stream()
            .map(proratedPeriod -> {
                var data = proratedPeriod.calculateProratedData();
                return new CalculationResult<ContractWithProductsAndSuspensions>(
                    data.contractId(),
                    ctx.billingStartDate(),
                    ctx.billingEndDate(),
                    data.productOfferingId(),
                    data.chargeItemId(),
                    data.revenueItemId(),
                    data.periodStartDate(),
                    data.periodEndDate(),
                    data.suspensionType().orElse(null),
                    data.proratedFee(),
                    data.balance(),
                    null,
                    null // BasicPolicyMonthlyFeeCalculator는 후처리가 필요 없음
                );
            })
            .filter(Objects::nonNull)
            .toList();
        return result;
    }
}