package me.realimpact.telecom.bill.prework.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvObjAcnt {
    private Long acntNum;
    private Long repSvcMgmtNum;
    private String billOperNum;
    private String billOperCyclCd;
    private String payMthdCd;
    private String billTypes;
}
