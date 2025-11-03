package me.realimpact.telecom.billing.batch.bill.reader.prework;

import lombok.extern.slf4j.Slf4j;
import me.realimpact.telecom.bill.prework.domain.InvObjAcnt;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BillOperNumAssignReader implements ItemStreamReader<InvObjAcnt> {

    private final SqlSessionFactory sqlSessionFactory;
    private final int partitionKey;
    private final int partitionCount;
    private final String invOperCyclCd;

    private MyBatisCursorItemReader<InvObjAcnt> delegate;

    public BillOperNumAssignReader(SqlSessionFactory sqlSessionFactory, int partitionKey, int partitionCount, String invOperCyclCd) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.partitionKey = partitionKey;
        this.partitionCount = partitionCount;
        this.invOperCyclCd = invOperCyclCd;
        log.info("=== BillOperNumAssignReader created for partition {}/{} ===", partitionKey, partitionCount);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("=== BillOperNumAssignReader.open() called for partition {} ===", partitionKey);
        delegate = new MyBatisCursorItemReader<>();
        delegate.setSqlSessionFactory(sqlSessionFactory);
        delegate.setQueryId("me.realimpact.telecom.bill.prework.infrastructure.mapper.InvObjAcntMapper.findInvObjAcntByPartition");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("partitionKey", partitionKey);
        parameters.put("partitionCount", partitionCount);
        parameters.put("invOperCyclCd", invOperCyclCd);
        delegate.setParameterValues(parameters);

        delegate.open(executionContext);
        log.info("=== Internal MyBatisCursorItemReader opened for partition {} ===", partitionKey);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (delegate != null) {
            delegate.update(executionContext);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        if (delegate != null) {
            delegate.close();
            log.info("=== Internal MyBatisCursorItemReader closed for partition {} ===", partitionKey);
        }
    }

    @Override
    public InvObjAcnt read() throws Exception {
        if (delegate == null) {
            return null;
        }
        return delegate.read();
    }
}
