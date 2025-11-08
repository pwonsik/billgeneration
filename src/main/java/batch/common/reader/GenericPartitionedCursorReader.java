package batch.common.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.util.Map;

@Slf4j
public class GenericPartitionedCursorReader<T> implements ItemStreamReader<T> {

    private final SqlSessionFactory sqlSessionFactory;
    private final String queryId;
    private final Map<String, Object> parameters;

    private MyBatisCursorItemReader<T> delegate;

    public GenericPartitionedCursorReader(SqlSessionFactory sqlSessionFactory, String queryId, Map<String, Object> parameters) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.queryId = queryId;
        this.parameters = parameters;
        log.info("=== GenericPartitionedCursorReader created for query: {} ===", queryId);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("=== GenericPartitionedCursorReader.open() called for query: {} ===", queryId);
        delegate = new MyBatisCursorItemReader<>();
        delegate.setSqlSessionFactory(sqlSessionFactory);
        delegate.setQueryId(queryId);
        delegate.setParameterValues(parameters);
        delegate.open(executionContext);
        log.info("=== Internal MyBatisCursorItemReader opened for query: {} ===", queryId);
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
            log.info("=== Internal MyBatisCursorItemReader closed for query: {} ===", queryId);
        }
    }

    @Override
    public T read() throws Exception {
        if (delegate == null) {
            return null;
        }
        return delegate.read();
    }
}
