package org.meveo.cassandra.dao;

import java.util.UUID;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;

import org.meveo.cassandra.model.RatedTransaction;

@Dao
public interface RatedTransactionDao {
    @Select
    RatedTransaction findById(UUID id);

    @Insert
    void save(RatedTransaction ratedTransaction);

    @Delete
    void delete(RatedTransaction ratedTransaction);

    @Query("SELECT * FROM rating.rated_transaction")
    PagingIterable<RatedTransaction> all();

    @Select(customWhereClause = "subscription_id = :id ALLOW FILTERING")
    PagingIterable<RatedTransaction> findBySubscriptionId(@CqlName("id") long id);
}