package me.realimpact.telecom.billing.batch.bill.reader.prework;

import me.realimpact.telecom.bill.prework.domain.InvObjAcnt;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;

import java.util.HashMap;
import java.util.Map;

public class BillOperNumAssignReader {

    private BillOperNumAssignReader() {
        // private constructor
    }

    public static MyBatisCursorItemReader<InvObjAcnt> newInstance(SqlSessionFactory sqlSessionFactory, int partitionKey, int partitionCount, String invOperCyclCd) {
        MyBatisCursorItemReader<InvObjAcnt> reader = new MyBatisCursorItemReader<>();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("partitionKey", partitionKey);
        parameters.put("partitionCount", partitionCount);
        parameters.put("invOperCyclCd", invOperCyclCd);

        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("me.realimpact.telecom.bill.prework.infrastructure.mapper.InvObjAcntMapper.findInvObjAcntByPartition");
        reader.setParameterValues(parameters);

        return reader;
    }
}
