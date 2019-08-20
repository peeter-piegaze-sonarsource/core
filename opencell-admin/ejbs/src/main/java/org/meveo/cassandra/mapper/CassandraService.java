package org.meveo.cassandra.mapper;

import java.net.InetSocketAddress;

import com.datastax.oss.driver.api.core.CqlSession;

import org.meveo.cassandra.dao.RatedTransactionDao;
import org.meveo.cassandra.dao.WalletOperationDao;
import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraService {


    private static final Logger log = LoggerFactory.getLogger(CassandraService.class);

    private static CassandraService instance;

    private RatedTransactionDao rtDao;

    private WalletOperationDao woDao;

    private CassandraService() {
        initSession();
    }

    private void initSession() {
        String keyspace = ParamBean.getInstance().getProperty("cassandra.keyspace", "meveo");
        String host = ParamBean.getInstance().getProperty("cassandra.host", "127.0.0.1");
        String port = ParamBean.getInstance().getProperty("cassandra.port", "9042");
        String datacenter = ParamBean.getInstance().getProperty("cassandra.datacenter", "datacenter1");
        log.info("Cassandra init session using host : {}, port : {} and keyspace : {}", host, port, keyspace);
        CqlSession session = CqlSession.builder().addContactPoint(new InetSocketAddress(host, Integer.parseInt(port)))
                .withKeyspace(keyspace)
                .withLocalDatacenter(datacenter).build();
        RatedTransactionMapper rtMapper= new RatedTransactionMapperBuilder(session).build();
        rtDao = rtMapper.ratedTransacationDao(keyspace, "rated_transaction");

        WalletOperationMapper woMapper = new WalletOperationMapperBuilder(session).build();
        woDao = woMapper.walletOperationDao(keyspace, "wallet_operation");
    }

    public static CassandraService getInstance() {
        if (instance == null) {
            log.info("Creating a new instance : ");
            instance = new CassandraService();
        }

        return instance;
    }

    public RatedTransactionDao getRatedTransactionDao() {
       return rtDao;
    }

    public WalletOperationDao getWalletOperationDao() {
        return woDao;
    }
}
