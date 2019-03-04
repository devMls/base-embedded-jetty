/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.config;

import util.server.JettyServer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.javalite.activeweb.AbstractDBConfig;
import org.javalite.activeweb.AppContext;
import static org.javalite.app_config.AppConfig.p;

/**

 @author mlarr
 */
public class DbConfig extends AbstractDBConfig {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DbConfig.class);

    @Override
    public void init(AppContext context) {
        init();
    }

    public void init() {

        environment("development", true).dataSource(JettyServer.dataSource);

        environment("development").testing().dataSource(JettyServer.dataSource);

        environment("stage", true).dataSource(JettyServer.dataSource);

        environment("production", true).dataSource(JettyServer.dataSource);

        LogsConfig.initLogging(JettyServer.dataSource);

        migrateDB(JettyServer.dataSource);
    }

    private void migrateDB(HikariDataSource dataSource) {

        Flyway flyway = new Flyway();

        flyway.setDataSource(dataSource);

        try {
            flyway.repair();  //vuelve a intentar los que hayan fallado
            flyway.migrate();
            log.info("migracion realizada");

        } catch (Exception e) {
            log.error("error migrando base de datos:", e);
        }
    }

    public static HikariDataSource generateDataSource() {
        HikariConfig config = new HikariConfig();

        //database access
        config.setJdbcUrl(p("dataSource.JdbcUrl"));
        config.setUsername(p("dataSource.setUsername"));
        config.setPassword(p("dataSource.setPassword"));
        config.setDriverClassName(p("dataSource.Driver"));

        //database config
        config.addDataSourceProperty("cachePrepStmts", p("dataSource.cachePrepStmts"));
        config.addDataSourceProperty("prepStmtCacheSize", p("dataSource.prepStmtCacheSize"));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", p("dataSource.prepStmtCacheSqlLimit"));
        config.addDataSourceProperty("useServerPrepStmts", p("dataSource.useServerPrepStmts"));
        config.addDataSourceProperty("useLocalSessionState", p("dataSource.useLocalSessionState"));
        config.addDataSourceProperty("useLocalTransactionState", p("dataSource.useLocalTransactionState"));
        config.addDataSourceProperty("rewriteBatchedStatements", p("dataSource.rewriteBatchedStatements"));
        config.addDataSourceProperty("cacheResultSetMetadata", p("dataSource.cacheResultSetMetadata"));
        config.addDataSourceProperty("cacheServerConfiguration", p("dataSource.cacheServerConfiguration"));
        config.addDataSourceProperty("elideSetAutoCommits", p("dataSource.elideSetAutoCommits"));
        config.addDataSourceProperty("maintainTimeStats", p("dataSource.maintainTimeStats"));

        config.setMaximumPoolSize(Integer.valueOf(p("dataSource.maximumPoolSize")));
        config.setMinimumIdle(Integer.valueOf(p("dataSource.minimumIdle")));

        try {
            return new HikariDataSource(config);
        } catch (Exception e) {
            log.error("error creando datasource:", e);
            return null;
        }
    }

}
