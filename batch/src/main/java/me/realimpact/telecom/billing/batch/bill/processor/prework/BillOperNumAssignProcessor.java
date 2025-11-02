package me.realimpact.telecom.billing.batch.bill.processor.prework;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.realimpact.telecom.bill.prework.domain.InvObjAcnt;

@Slf4j
@Component
public class BillOperNumAssignProcessor implements ItemProcessor<InvObjAcnt, InvObjAcnt> {

    @Override
    public InvObjAcnt process(InvObjAcnt item) throws Exception {
        log.debug("Processing InvObjAcnt: {}", item.getAcntNum());

        String billTypes = item.getBillTypes();
        if (billTypes != null && billTypes.contains("1") && billTypes.contains("2")) {
            item.setBillOperNum("SP001");
        } else {
            item.setBillOperNum("SE001");
        }
        
        item.setBillOperCyclCd("01"); // Example fixed value

        return item;
    }
}
