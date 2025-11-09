package bill.policy.onetimecharge.adapter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import bill.policy.onetimecharge.adapter.mybatis.DeviceInstallmentMapper;
import bill.policy.onetimecharge.converter.OneTimeChargeDtoConverter;
import bill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;
import bill.policy.onetimecharge.dto.DeviceInstallmentDto;
import bill.policy.onetimecharge.port.DeviceInstallmentQueryPort;

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
