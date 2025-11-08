package bill.policy.onetimecharge.loader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import bill.application.onetimecharge.OneTimeChargeDataLoader;
import bill.domain.CalculationContext;
import bill.domain.onetimecharge.OneTimeChargeDomain;
import bill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;
import bill.policy.onetimecharge.port.DeviceInstallmentQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DeviceInstallmentMaster 데이터 로딩 전담 클래스
 * 단일 책임: 단말할부금 데이터 로딩
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceInstallmentMasterDataLoader implements OneTimeChargeDataLoader<DeviceInstallmentMaster> {

    private final DeviceInstallmentQueryPort deviceInstallmentQueryPort;

    @Override
    public Class<DeviceInstallmentMaster> getDomainType() {
        return DeviceInstallmentMaster.class;
    }

    @Override
    public Map<Long, List<? extends OneTimeChargeDomain>> read(List<Long> contractIds, CalculationContext ctx) {
        log.debug("Loading DeviceInstallmentMaster data for {} contracts", contractIds.size());

        Map<Long, List<DeviceInstallmentMaster>> specificData = deviceInstallmentQueryPort
                .findDeviceInstallments(contractIds, ctx.billingStartDate(), ctx.billingEndDate())
                .stream()
                .collect(Collectors.groupingBy(DeviceInstallmentMaster::getContractId));

        log.debug("Loaded InstallationHistory data for {} contracts", specificData.size());

        return specificData.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}