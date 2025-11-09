package bill.policy.onetimecharge.port;

import java.time.LocalDate;
import java.util.List;

import bill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;

public interface DeviceInstallmentQueryPort {
    List<DeviceInstallmentMaster> findDeviceInstallments(List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate);
}
