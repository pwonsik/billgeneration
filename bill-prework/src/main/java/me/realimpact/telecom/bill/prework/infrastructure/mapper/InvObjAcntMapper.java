package me.realimpact.telecom.bill.prework.infrastructure.mapper;

import me.realimpact.telecom.bill.prework.domain.InvObjAcnt;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InvObjAcntMapper {
    List<InvObjAcnt> findAllInvObjAcnt(@Param("invOperCyclCd") String invOperCyclCd);
    void updateInvObjAcnt(InvObjAcnt invObjAcnt);
}
