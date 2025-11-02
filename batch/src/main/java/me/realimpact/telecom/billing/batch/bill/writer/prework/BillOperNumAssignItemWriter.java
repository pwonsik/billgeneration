package me.realimpact.telecom.billing.batch.bill.writer.prework;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;

import me.realimpact.telecom.bill.prework.domain.InvObjAcnt;

public class BillOperNumAssignItemWriter {
    private static final String MAPPER_STATEMENT_ID = "me.realimpact.telecom.bill.prework.infrastructure.mapper.InvObjAcntMapper.updateInvObjAcnt";

    private BillOperNumAssignItemWriter() {
        // Not meant to be instantiated
    }

    public static MyBatisBatchItemWriter<InvObjAcnt> newInstance(SqlSessionFactory sqlSessionFactory) {
        return new MyBatisBatchItemWriterBuilder<InvObjAcnt>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId(MAPPER_STATEMENT_ID)
                .build();
    }
}
