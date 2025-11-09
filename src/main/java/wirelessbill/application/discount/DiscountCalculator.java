package wirelessbill.application.discount;

import lombok.RequiredArgsConstructor;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationResult;
import wirelessbill.domain.discount.ContractDiscounts;
import wirelessbill.domain.discount.Discount;
import wirelessbill.port.out.ContractDiscountCommandPort;
import wirelessbill.port.out.ContractDiscountQueryPort;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 계약 할인 계산기
 * 중첩 구조의 ContractDiscount를 처리하여 각 할인별로 CalculationResult를 생성한다.
 */
@Service
@Order(22)
@RequiredArgsConstructor
public class DiscountCalculator {
    private final ContractDiscountQueryPort contractDiscountQueryPort;
    private final ContractDiscountCommandPort contractDiscountCommandPort;

    public Map<Long, ContractDiscounts> read(CalculationContext ctx, List<Long> contractIds) {
        return contractDiscountQueryPort.findContractDiscounts(
            contractIds, ctx.billingStartDate(), ctx.billingEndDate()
        ).stream().collect(
            Collectors.toMap(ContractDiscounts::contractId, Function.identity())
        );
    }

    public List<CalculationResult<?>> process(CalculationContext ctx,
                                              List<CalculationResult<?>> proratedCalculationResults,
                                              List<Discount> discounts) {
        return discounts.stream()
                .flatMap(discount -> proratedCalculationResults.stream()
                        .map(calcResult -> applyDiscountToResult(discount, calcResult))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                )
                .collect(Collectors.toList());
    }

    private Optional<CalculationResult<?>> applyDiscountToResult(Discount discount, CalculationResult<?> calcResult) {
        if (!discount.isDiscountTarget(calcResult)) {
            return Optional.empty();
        }

        BigDecimal discountAmount = discount.calculateDiscount(calcResult);
        // 할인액이 0보다 클 때만 처리
        if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        // 부수 효과(Side effect): 원본 계산 결과의 잔액을 차감합니다.
        // 이 로직은 현재 파이프라인 구조에서 허용된 방식으로 동작합니다.
        calcResult.debitBalance(discountAmount);

        CalculationResult<?> discountResult = new CalculationResult<>(
                calcResult.getContractId(),
                calcResult.getBillingStartDate(),
                calcResult.getBillingEndDate(),
                calcResult.getProductOfferingId(),
                "DC",   // 임시
                "DC",  // 임시
                calcResult.getEffectiveStartDate(),
                calcResult.getEffectiveEndDate(),
                calcResult.getSuspensionType(),
                discountAmount.negate(),
                BigDecimal.ZERO,
                discount,
                this::post
        );
        return Optional.of(discountResult);
    }


    /**
     * 할인 처리 완료 후 상태 업데이트
     */
    public void post(CalculationContext ctx, Discount input) {
        if (ctx.billingCalculationType().isPostable()) {
            contractDiscountCommandPort.applyDiscount(input);
        }
    }
}