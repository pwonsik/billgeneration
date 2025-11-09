package bill.policy.onetimecharge.port;

import java.time.LocalDate;
import java.util.List;

import bill.policy.onetimecharge.domain.InstallationHistory;

public interface InstallationHistoryQueryPort {
    List<InstallationHistory> findInstallations(List<Long> contractIds, LocalDate billingStartDate, LocalDate billingEndDate);
}
