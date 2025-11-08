package me.realimpact.telecom.calculation.application;

import lombok.extern.slf4j.Slf4j;
import me.realimpact.telecom.calculation.application.discount.DiscountCalculator;
import me.realimpact.telecom.calculation.application.monthlyfee.MonthlyFeeDataLoader;
import me.realimpact.telecom.calculation.application.onetimecharge.OneTimeChargeDataLoader;
import me.realimpact.telecom.calculation.domain.CalculationContext;
import me.realimpact.telecom.calculation.domain.CalculationTarget;
import me.realimpact.telecom.calculation.domain.discount.ContractDiscounts;
import me.realimpact.telecom.calculation.domain.monthlyfee.MonthlyChargeDomain;
import me.realimpact.telecom.calculation.domain.onetimecharge.OneTimeChargeDomain;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CalculationTargetLoader {
    private final DiscountCalculator discountCalculator;
    private final Map<Class<? extends MonthlyChargeDomain>, MonthlyFeeDataLoader<? extends MonthlyChargeDomain>> monthlyFeeDataLoaderMap;
    private final Map<Class<? extends OneTimeChargeDomain>, OneTimeChargeDataLoader<? extends OneTimeChargeDomain>> oneTimeChargeDataLoaderMap;

    public CalculationTargetLoader(
            DiscountCalculator discountCalculator,
            List<MonthlyFeeDataLoader<? extends MonthlyChargeDomain>> monthlyFeeDataLoaders,
            List<OneTimeChargeDataLoader<? extends OneTimeChargeDomain>> oneTimeChargeDataLoaders) {
        this.discountCalculator = discountCalculator;

        this.monthlyFeeDataLoaderMap = monthlyFeeDataLoaders.stream()
                .collect(Collectors.toMap(
                        MonthlyFeeDataLoader::getDomainType,
                        Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        this.oneTimeChargeDataLoaderMap = oneTimeChargeDataLoaders.stream()
                .collect(Collectors.toMap(
                        OneTimeChargeDataLoader::getDomainType,
                        Function.identity(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        log.info("Registered {} MonthlyFee DataLoaders: {}",
                monthlyFeeDataLoaders.size(),
                monthlyFeeDataLoaders.stream()
                        .map(loader -> loader.getDomainType().getSimpleName())
                        .collect(Collectors.joining(", ")));

        log.info("Registered {} OneTimeCharge DataLoaders: {}",
                oneTimeChargeDataLoaders.size(),
                oneTimeChargeDataLoaders.stream()
                        .map(loader -> loader.getDomainType().getSimpleName())
                        .collect(Collectors.joining(", ")));
    }

    public List<CalculationTarget> load(List<Long> contractIds, CalculationContext ctx) {
        List<CalculationTarget> calculationTargets = new BulkDataPipeline(contractIds, ctx)
                .loadMonthlyFees(monthlyFeeDataLoaderMap)
                .loadOneTimeCharges(oneTimeChargeDataLoaderMap)
                .loadDiscounts(discountCalculator)
                .assembleTargets();

        log.info("********* 생성된 calculationTargets 개수: {}", calculationTargets.size());
        return calculationTargets;
    }

    private static class BulkDataPipeline {
        private final List<Long> contractIds;
        private final CalculationContext context;

        private Map<Class<? extends MonthlyChargeDomain>, Map<Long, List<? extends MonthlyChargeDomain>>> monthlyFeeDataByType;
        private Map<Class<? extends OneTimeChargeDomain>, Map<Long, List<? extends OneTimeChargeDomain>>> oneTimeChargeDataByType;
        private Map<Long, ContractDiscounts> contractDiscountsMap;

        BulkDataPipeline(List<Long> contractIds, CalculationContext context) {
            this.contractIds = contractIds;
            this.context = context;
        }

        public BulkDataPipeline loadMonthlyFees(Map<Class<? extends MonthlyChargeDomain>, MonthlyFeeDataLoader<? extends MonthlyChargeDomain>> dataLoaders) {
            this.monthlyFeeDataByType = new HashMap<>();
            for (var entry : dataLoaders.entrySet()) {
                var dataType = entry.getKey();
                var loader = entry.getValue();
                Map<Long, List<? extends MonthlyChargeDomain>> data = loader.read(contractIds, context);
                if (!data.isEmpty()) {
                    this.monthlyFeeDataByType.put(dataType, data);
                }
            }
            return this;
        }

        public BulkDataPipeline loadOneTimeCharges(Map<Class<? extends OneTimeChargeDomain>, OneTimeChargeDataLoader<? extends OneTimeChargeDomain>> dataLoaders) {
            this.oneTimeChargeDataByType = new HashMap<>();
            for (var entry : dataLoaders.entrySet()) {
                var dataType = entry.getKey();
                var loader = entry.getValue();
                Map<Long, List<? extends OneTimeChargeDomain>> data = loader.read(contractIds, context);
                if (!data.isEmpty()) {
                    this.oneTimeChargeDataByType.put(dataType, data);
                }
            }
            return this;
        }

        public BulkDataPipeline loadDiscounts(DiscountCalculator discountCalculator) {
            this.contractDiscountsMap = discountCalculator.read(context, contractIds);
            return this;
        }

        public List<CalculationTarget> assembleTargets() {
            return contractIds.stream()
                    .map(this::createTargetForContract)
                    .toList();
        }

        private CalculationTarget createTargetForContract(Long contractId) {
            var monthlyFeeData = groupDataByContract(contractId, monthlyFeeDataByType);
            var oneTimeChargeData = groupDataByContract(contractId, oneTimeChargeDataByType);
            var discounts = Optional.ofNullable(contractDiscountsMap.get(contractId))
                    .map(ContractDiscounts::discounts)
                    .orElse(Collections.emptyList());

            return new CalculationTarget(contractId, monthlyFeeData, oneTimeChargeData, discounts);
        }

        private <T> Map<Class<? extends T>, List<? extends T>> groupDataByContract(
                Long contractId,
                Map<Class<? extends T>, Map<Long, List<? extends T>>> dataByType) {

            Map<Class<? extends T>, List<? extends T>> result = new HashMap<>();
            if (dataByType == null) {
                return result;
            }

            for (var entry : dataByType.entrySet()) {
                var dataType = entry.getKey();
                var dataByContract = entry.getValue();
                List<? extends T> contractData = dataByContract.get(contractId);
                if (contractData != null && !contractData.isEmpty()) {
                    result.put(dataType, contractData);
                }
            }
            return result;
        }
    }
}
