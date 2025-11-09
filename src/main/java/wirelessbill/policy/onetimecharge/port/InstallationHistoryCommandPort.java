package bill.policy.onetimecharge.port;

import bill.policy.onetimecharge.domain.InstallationHistory;

public interface InstallationHistoryCommandPort {
    void updateChargeStatus(InstallationHistory installationHistory);
}
