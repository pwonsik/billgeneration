package wirelessbill.port.out;

import java.time.LocalDate;
import java.util.Map;

import wirelessbill.domain.masterdata.RevenueMasterData;

public interface RevenueMasterDataQueryPort {
    Map<String, RevenueMasterData> findRevenueMasterDataByBaseDate(LocalDate baseDate);
}
