package bill.application.pipeline.steps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bill.application.monthlyfee.MonthlyFeeCalculator;
import bill.application.onetimecharge.OneTimeChargeCalculator;
import bill.application.pipeline.CalculationStep;
import bill.application.pipeline.PipelineContext;
import bill.domain.CalculationResult;
import bill.domain.monthlyfee.MonthlyChargeDomain;
import bill.domain.onetimecharge.OneTimeChargeDomain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InitializeStep implements CalculationStep {
    private final List<MonthlyFeeCalculator<? extends MonthlyChargeDomain>> monthlyFeeCalculators;
    private final List<OneTimeChargeCalculator<? extends OneTimeChargeDomain>> oneTimeChargeCalculators;

    @Override
    public void execute(PipelineContext context) {
        List<CalculationResult<?>> initialResults = Stream.concat(
                calculateMonthlyFees(context).stream(),
                calculateOneTimeCharges(context).stream()
        ).collect(Collectors.toList());
        context.setCalculationResults(initialResults);
    }

    private List<CalculationResult<?>> calculateMonthlyFees(PipelineContext context) {
        return monthlyFeeCalculators.stream()
                .flatMap(calculator -> runMonthlyFeeCalculator(calculator, context).stream())
                .collect(Collectors.toList());
    }

    private List<CalculationResult<?>> calculateOneTimeCharges(PipelineContext context) {
        return oneTimeChargeCalculators.stream()
                .flatMap(calculator -> runOneTimeChargeCalculator(calculator, context).stream())
                .collect(Collectors.toList());
    }

    private <T extends MonthlyChargeDomain> List<CalculationResult<?>> runMonthlyFeeCalculator(MonthlyFeeCalculator<T> calculator, PipelineContext context) {
        List<T> inputData = context.getCalculationTarget().getMonthlyChargeData(calculator.getDomainType());
        return processItems(inputData, calculator::process, context);
    }

    private <T extends OneTimeChargeDomain> List<CalculationResult<?>> runOneTimeChargeCalculator(OneTimeChargeCalculator<T> calculator, PipelineContext context) {
        List<T> inputData = context.getCalculationTarget().getOneTimeChargeData(calculator.getDomainType());
        return processItems(inputData, calculator::process, context);
    }

    private <T> List<CalculationResult<?>> processItems(
            Collection<T> items,
            BiFunction<bill.domain.CalculationContext, T, List<? extends CalculationResult<?>>> processor,
            PipelineContext context
    ) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .flatMap(item -> processor.apply(context.getCalculationContext(), item).stream())
                .collect(Collectors.toList());
    }
}
