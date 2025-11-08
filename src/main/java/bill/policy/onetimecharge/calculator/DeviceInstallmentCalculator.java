package bill.policy.onetimecharge.calculator;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import bill.application.onetimecharge.OneTimeChargeCalculator;
import bill.domain.CalculationContext;
import bill.domain.CalculationResult;
import bill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;
import lombok.RequiredArgsConstructor;

/**
 * 단말할부금 계산기
 * 단일 책임: 단말할부금 계산 로직만 담당
 */
@Component
@Order(21)
@RequiredArgsConstructor
public class DeviceInstallmentCalculator implements OneTimeChargeCalculator<DeviceInstallmentMaster> {

    @Override
    public List<CalculationResult<DeviceInstallmentMaster>> process(CalculationContext ctx, DeviceInstallmentMaster input) {
        BigDecimal fee = BigDecimal.valueOf(input.getFee(ctx.billingCalculationType(), ctx.billingCalculationPeriod()));
        BigDecimal balance = new BigDecimal(fee.unscaledValue(), fee.scale());
        return List.of(
                new CalculationResult<>(
                        input.getContractId(),
                        ctx.billingStartDate(),
                        ctx.billingEndDate(),
                        "HALBU",
                        "HALBU",
                        "HALBU",
                        ctx.billingStartDate(),
                        ctx.billingEndDate(),
                        null,
                        fee,
                        balance,
                        input,
                        this::post
                )
        );
    }


    public void post(CalculationContext ctx, DeviceInstallmentMaster input) {
        if (ctx.billingCalculationType().isPostable()) {
            //deviceInstallmentCommandPort.updateChargeStatus(input);
        }
    }
    
    // OneTimeChargeCalculator 인터페이스 구현
    @Override
    public Class<DeviceInstallmentMaster> getDomainType() {
        return DeviceInstallmentMaster.class;
    }

}