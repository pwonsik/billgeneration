package me.realimpact.telecom.calculation.port.out;

import me.realimpact.telecom.calculation.domain.masterdata.RevenueMasterData;

import java.time.LocalDate;
import java.util.Map;

public interface RevenueMasterDataQueryPort {
    Map<String, RevenueMasterData> findRevenueMasterDataByBaseDate(LocalDate baseDate);
}
