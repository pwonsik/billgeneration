package wirelessbill.policy.onetimecharge.adapter;

import lombok.RequiredArgsConstructor;
import wirelessbill.policy.onetimecharge.adapter.mybatis.DeviceInstallmentMapper;
import wirelessbill.policy.onetimecharge.converter.OneTimeChargeDtoConverter;
import wirelessbill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;
import wirelessbill.policy.onetimecharge.dto.DeviceInstallmentDto;
import wirelessbill.policy.onetimecharge.port.DeviceInstallmentQueryPort;

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
