package me.realimpact.telecom.calculation.policy.onetimecharge.adapter;

import lombok.RequiredArgsConstructor;
import me.realimpact.telecom.calculation.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;
import me.realimpact.telecom.calculation.policy.onetimecharge.adapter.mybatis.DeviceInstallmentMapper;
import me.realimpact.telecom.calculation.policy.onetimecharge.converter.OneTimeChargeDtoConverter;
import me.realimpact.telecom.calculation.policy.onetimecharge.dto.DeviceInstallmentDto;
import me.realimpact.telecom.calculation.policy.onetimecharge.port.DeviceInstallmentQueryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceInstallmentRepository implements DeviceInstallmentQueryPort {
    private final DeviceInstallmentMapper deviceInstallmentMapper;
    private final OneTimeChargeDtoConverter oneTimeChargeDtoConverter;

    @Override
    public List<DeviceInstallmentMaster> findDeviceInstallments(
        List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate
    ) {
        List<DeviceInstallmentDto> deviceInstallmentDtos =
            deviceInstallmentMapper.findInstallmentsByContractIds(contractIds, billingEndDate);
        return oneTimeChargeDtoConverter.convertToDeviceInstallmentMasters(deviceInstallmentDtos);
    }

}
