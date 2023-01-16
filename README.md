# base embedded jetty (belogic repository in bitbucket is more advanced)
javalite activeweb in a embedded jetty example


global.properties needed in resources/app_config with this parameters:

startPort = 8003<br/>
stopPort = 8004<br/>
contextPath=/<br/><br/>
dispacher.path=/*<br/>
dispacher.exclusions=css,images,js,ico,woff,svg,png<br/>
dispacher.root_controller=home<br/><br/>
dataSource.JdbcUrl=<br/>
dataSource.setUsername=<br/>
dataSource.setPassword=<br/>
dataSource.cachePrepStmts=true<br/>
dataSource.prepStmtCacheSize=250<br/>
dataSource.prepStmtCacheSqlLimit=2048<br/>
dataSource.useServerPrepStmts=true<br/>
dataSource.useLocalSessionState=true<br/>
dataSource.useLocalTransactionState=true<br/>
dataSource.rewriteBatchedStatements=true<br/>
dataSource.cacheResultSetMetadata=true<br/>
dataSource.cacheServerConfiguration=true<br/>
dataSource.elideSetAutoCommits=true<br/>
dataSource.maintainTimeStats=false<br/>
