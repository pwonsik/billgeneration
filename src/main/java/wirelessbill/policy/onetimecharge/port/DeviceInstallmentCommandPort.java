package wirelessbill.policy.onetimecharge.port;

import wirelessbill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;

public interface DeviceInstallmentCommandPort {
    void updateChargeStatus(DeviceInstallmentMaster deviceInstallmentMaster);
}
