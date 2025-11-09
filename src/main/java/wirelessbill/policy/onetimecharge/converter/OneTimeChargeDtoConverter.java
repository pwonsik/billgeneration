package wirelessbill.policy.onetimecharge.converter;

import org.springframework.stereotype.Component;

import wirelessbill.policy.onetimecharge.domain.InstallationHistory;
import wirelessbill.policy.onetimecharge.domain.installment.DeviceInstallmentDetail;
import wirelessbill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;
import wirelessbill.policy.onetimecharge.dto.DeviceInstallmentDetailDto;
import wirelessbill.policy.onetimecharge.dto.DeviceInstallmentDto;
import wirelessbill.policy.onetimecharge.dto.InstallationHistoryDto;

import java.util.List;

/**
 * 일회성 과금 도메인 객체와 DTO 간의 변환을 담당하는 컨버터
 */
@Component
public class OneTimeChargeDtoConverter {

    /**
     * InstallationHistoryDto를 InstallationHistory 도메인 객체로 변환
     */
    public InstallationHistory convertToInstallationHistory(InstallationHistoryDto dto) {
        return new InstallationHistory(
            dto.getContractId(),
            dto.getSequenceNumber(),
            dto.getInstallationDate(),
            dto.getInstallationFee().longValue(),
            dto.getBilledFlag()
        );
    }

    /**
     * InstallationHistoryDto 리스트를 InstallationHistory 도메인 객체 리스트로 변환
     */
    public List<InstallationHistory> convertToInstallationHistories(List<InstallationHistoryDto> dtos) {
        return dtos.stream()
            .map(this::convertToInstallationHistory)
            .toList();
    }

    /**
     * DeviceInstallmentDetailDto를 DeviceInstallmentDetail 도메인 객체로 변환
     */
    public DeviceInstallmentDetail convertToDeviceInstallmentDetail(DeviceInstallmentDetailDto dto) {
        return new DeviceInstallmentDetail(
            dto.getInstallmentRound(),
            dto.getInstallmentAmount().longValue()
        );
    }

    /**
     * DeviceInstallmentDto를 DeviceInstallmentMaster 도메인 객체로 변환
     */
    public DeviceInstallmentMaster convertToDeviceInstallmentMaster(DeviceInstallmentDto dto) {
        List<DeviceInstallmentDetail> details = dto.getDetails() != null ?
            dto.getDetails().stream()
                .map(this::convertToDeviceInstallmentDetail)
                .toList() : List.of();

        return new DeviceInstallmentMaster(
            dto.getContractId(),
            dto.getInstallmentSequence(),
            dto.getInstallmentStartDate(),
            dto.getTotalInstallmentAmount().longValue(),
            dto.getInstallmentMonths(),
            dto.getBilledCount(),
            details
        );
    }

    /**
     * DeviceInstallmentDto 리스트를 DeviceInstallmentMaster 도메인 객체 리스트로 변환
     */
    public List<DeviceInstallmentMaster> convertToDeviceInstallmentMasters(List<DeviceInstallmentDto> dtos) {
        return dtos.stream()
            .map(this::convertToDeviceInstallmentMaster)
            .toList();
    }

}