package me.realimpact.telecom.calculation.policy.onetimecharge.port;

import me.realimpact.telecom.calculation.policy.onetimecharge.domain.InstallationHistory;

public interface InstallationHistoryCommandPort {
    void updateChargeStatus(InstallationHistory installationHistory);
}
