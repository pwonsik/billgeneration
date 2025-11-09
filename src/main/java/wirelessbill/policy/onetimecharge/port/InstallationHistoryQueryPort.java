package wirelessbill.policy.onetimecharge.port;

import java.time.LocalDate;
import java.util.List;

import wirelessbill.policy.onetimecharge.domain.InstallationHistory;

public interface InstallationHistoryQueryPort {
    List<InstallationHistory> findInstallations(List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate);
}
