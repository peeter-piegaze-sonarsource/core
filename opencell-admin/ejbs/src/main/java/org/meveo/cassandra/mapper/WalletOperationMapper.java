package org.meveo.cassandra.mapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

import org.meveo.cassandra.dao.WalletOperationDao;

@Mapper
public interface WalletOperationMapper {
    @DaoFactory
    WalletOperationDao walletOperationDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    WalletOperationDao walletOperationDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    WalletOperationDao walletOperationDao(@DaoKeyspace String keyspace);
}