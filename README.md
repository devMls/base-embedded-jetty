# base_embedded_jetty
javalite activeweb in a embedded jetty example


global.properties needed in resources/app_config with this parameters:

startPort = 8003
stopPort = 8004
contextPath=/
dispacher.path=/*
dispacher.exclusions=css,images,js,ico,woff,svg,png
dispacher.root_controller=home
dataSource.JdbcUrl=
dataSource.setUsername=
dataSource.setPassword=
dataSource.cachePrepStmts=true
dataSource.prepStmtCacheSize=250
dataSource.prepStmtCacheSqlLimit=2048
dataSource.useServerPrepStmts=true
dataSource.useLocalSessionState=true
dataSource.useLocalTransactionState=true
dataSource.rewriteBatchedStatements=true
dataSource.cacheResultSetMetadata=true
dataSource.cacheServerConfiguration=true
dataSource.elideSetAutoCommits=true
dataSource.maintainTimeStats=false
