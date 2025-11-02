package me.realimpact.telecom.billing.batch.bill.reader.prework;

import me.realimpact.telecom.bill.prework.domain.InvObjAcnt;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;

import java.util.HashMap;
import java.util.Map;

public class BillOperNumAssignReader extends MyBatisPagingItemReader<InvObjAcnt> {

    private BillOperNumAssignReader() {
        super();
    }

    public static BillOperNumAssignReader newInstance(SqlSessionFactory sqlSessionFactory, int partitionKey, int partitionCount, String invOperCyclCd) {
        BillOperNumAssignReader reader = new BillOperNumAssignReader();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("partitionKey", partitionKey);
        parameters.put("partitionCount", partitionCount);
        parameters.put("invOperCyclCd", invOperCyclCd);

        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("me.realimpact.telecom.bill.prework.infrastructure.mapper.InvObjAcntMapper.findInvObjAcntByPartition");
        reader.setParameterValues(parameters);
        reader.setPageSize(100); // CHUNK_SIZE from BatchConstants

        return reader;
    }
}
