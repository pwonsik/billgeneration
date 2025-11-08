package bill.policy.onetimecharge.port;

import bill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;

public interface DeviceInstallmentCommandPort {
    void updateChargeStatus(DeviceInstallmentMaster deviceInstallmentMaster);
}
