package org.meveo.cassandra.dao;

import java.util.UUID;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;

import org.meveo.cassandra.model.WalletOperation;

@Dao
public interface WalletOperationDao {
    @Select
    WalletOperation findById(UUID id);

    @Insert
    void save(WalletOperation ratedTransaction);

    @Delete
    void delete(WalletOperation ratedTransaction);

    @Select(customWhereClause = "subscription_id = :id ALLOW FILTERING")
    PagingIterable<WalletOperation> findBySubscriptionId(@CqlName("id") long id);

    @Query("SELECT * FROM rating.wallet_operation")
    PagingIterable<WalletOperation> all();
}