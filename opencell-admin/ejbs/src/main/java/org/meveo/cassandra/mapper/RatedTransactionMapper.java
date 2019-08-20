package org.meveo.cassandra.mapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

import org.meveo.cassandra.dao.RatedTransactionDao;

@Mapper
public interface RatedTransactionMapper {
    @DaoFactory
    RatedTransactionDao ratedTransacationDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    RatedTransactionDao ratedTransacationDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    RatedTransactionDao ratedTransacationDao(@DaoKeyspace String keyspace);
}