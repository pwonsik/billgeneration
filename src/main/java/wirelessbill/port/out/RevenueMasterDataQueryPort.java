package bill.port.out;

import java.time.LocalDate;
import java.util.Map;

import bill.domain.masterdata.RevenueMasterData;

public interface RevenueMasterDataQueryPort {
    Map<String, RevenueMasterData> findRevenueMasterDataByBaseDate(LocalDate baseDate);
}
