package wirelessbill.application.prework;

public record PlanHistoryDto(
    Long contractId,
    Long acntNum,
    Long prtSeq,
    String bfPrcplnName,
    String afPrcplnName,
    String effStaDtm,
    String effEndDtm
) {
}


