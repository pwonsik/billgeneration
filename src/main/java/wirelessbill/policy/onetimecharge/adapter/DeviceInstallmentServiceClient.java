package bill.policy.onetimecharge.adapter;

import org.springframework.stereotype.Component;

import bill.policy.onetimecharge.domain.installment.DeviceInstallmentMaster;
import bill.policy.onetimecharge.port.DeviceInstallmentCommandPort;

@Component
public class DeviceInstallmentServiceClient implements DeviceInstallmentCommandPort {
    @Override
    public void updateChargeStatus(DeviceInstallmentMaster deviceInstallmentMaster) {
        // 오더의 서비스를 주입받아서 호출하거나 이벤트 발행 필요
    }
}
