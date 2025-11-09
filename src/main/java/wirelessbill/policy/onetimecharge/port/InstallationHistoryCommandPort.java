package wirelessbill.policy.onetimecharge.port;

import wirelessbill.policy.onetimecharge.domain.InstallationHistory;

public interface InstallationHistoryCommandPort {
    void updateChargeStatus(InstallationHistory installationHistory);
}
