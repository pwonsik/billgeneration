package bill.policy.onetimecharge.calculator;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import bill.application.onetimecharge.OneTimeChargeCalculator;
import bill.domain.CalculationContext;
import bill.domain.CalculationResult;
import bill.policy.onetimecharge.domain.InstallationHistory;
import lombok.RequiredArgsConstructor;

/**
 * 설치비 계산기
 * 단일 책임: 설치비 계산 로직만 담당
 */
@RequiredArgsConstructor
@Component
@Order(23)
public class InstallationFeeCalculator implements OneTimeChargeCalculator<InstallationHistory> {

    @Override
    public List<CalculationResult<InstallationHistory>> process(CalculationContext ctx, InstallationHistory input) {
        return List.of(
                new CalculationResult<>(
                    input.getContractId(),
                    ctx.billingStartDate(),
                    ctx.billingEndDate(),
                    "INST",
                    "INST",
                    "INST",
                    ctx.billingStartDate(),
                    ctx.billingEndDate(),
                    null,
                    BigDecimal.valueOf(input.getFee()),
                    BigDecimal.valueOf(input.getFee()),
                    input,
                    this::post
                )
        );
    }

    private void post(CalculationContext ctx, InstallationHistory input) {
        if (ctx.billingCalculationType().isPostable()) {
            //installationHistoryCommandPort.updateChargeStatus(input);
        }
    }
    
    // OneTimeChargeCalculator 인터페이스 구현
    @Override
    public Class<InstallationHistory> getDomainType() {
        return InstallationHistory.class;
    }

}