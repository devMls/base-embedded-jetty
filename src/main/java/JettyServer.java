
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.session.DatabaseAdaptor;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import static org.javalite.app_config.AppConfig.p;

public class JettyServer {

    private static org.eclipse.jetty.server.Server server;
    public static HikariDataSource dataSource;

    public static void main(String[] args) throws Exception {

        if (args.length > 0 && args[0].equalsIgnoreCase("stop")) {
            stop();
        } else {
            start();
        }
    }

    private static void stop() {

        try {
            System.out.println("Stopping....");
            String urlString = "http://localhost:" + p("stopPort") + "/stop";
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            is.close();
            System.out.println("Stopped");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void start() {

        server = new org.eclipse.jetty.server.Server();

        configureDatabase();
        
        createConnectors();

        WebAppContext appHandler = crateAppHandler();

        SessionHandler sessionHandler = createSessionHandler();

        appHandler.setSessionHandler(sessionHandler);

        //stop Handler
        StopHandler stopHandler = new StopHandler();
        stopHandler.setVirtualHosts(new String[]{"@stopConnector"});
        stopHandler.setContextPath("/stop");

        HandlerCollection collection = new HandlerCollection(appHandler, stopHandler);

        StatisticsHandler statsHandler = new StatisticsHandler();
        statsHandler.setHandler(collection);

        server.setHandler(statsHandler);
        server.setStopTimeout(3000);
        server.setStopAtShutdown(true);

        try {
            server.start();
            server.dumpStdErr();
            server.join();
            System.out.println("finalling main");
        } catch (Exception t) {
            t.printStackTrace();
        } finally {
            internalStop();

        }

    }

    private static void createConnectors() {
        //connectors for app and stop
        ServerConnector appConnector = new ServerConnector(server);
        appConnector.setPort(Integer.valueOf(p("startPort")));
        appConnector.setName("appConnector");
        ServerConnector stopConnector = new ServerConnector(server);
        stopConnector.setPort(Integer.valueOf(p("stopPort")));
        stopConnector.setName("stopConnector");
        //mount all in the server
        Connector[] connectors = {appConnector, stopConnector};
        server.setConnectors(connectors);

    }

    private static SessionHandler createSessionHandler() {
        //session in database
        final DefaultSessionIdManager idmgr = new DefaultSessionIdManager(server);
        idmgr.setServer(server);
        server.setSessionIdManager(idmgr);
        JDBCSessionDataStore sessionStore = new JDBCSessionDataStore();
        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDatasource(dataSource);
        sessionStore.setDatabaseAdaptor(databaseAdaptor);
        SessionHandler sessionHandler = new SessionHandler();
        DefaultSessionCache cacheSession = new DefaultSessionCache(sessionHandler);
        sessionHandler.setSessionCache(cacheSession);
        cacheSession.setSessionDataStore(sessionStore);
        sessionHandler.setSessionIdManager(idmgr);

        return sessionHandler;
    }

    private static WebAppContext crateAppHandler() {

        WebAppContext appHandler = new WebAppContext();
        appHandler.setContextPath("/");
        URL webAppDir = Thread.currentThread().getContextClassLoader().getResource("./");
        if (webAppDir == null) {
            throw new RuntimeException(String.format("No directory was found into the JAR file"));
        }
        try {
            appHandler.setResourceBase(webAppDir.toURI().toString());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        appHandler.setParentLoaderPriority(true);
        FilterHolder filter = appHandler.addFilter(org.javalite.activeweb.RequestDispatcher.class, p("dispacher.path"), EnumSet.of(DispatcherType.REQUEST));
        filter.setInitParameter("exclusions", p("dispacher.exclusions"));
        filter.setInitParameter("root_controller", p("dispacher.root_controller"));
        appHandler.setVirtualHosts(new String[]{"@appConnector"});

        return appHandler;
    }

    protected static void internalStop() {
        if (server != null && server.isRunning()) {

            try {
                server.setStopTimeout(1000);
                server.stop();
                if (dataSource != null && !dataSource.isClosed()) {
                    dataSource.close();
                }
                System.out.println("finalled");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    private static void configureDatabase() {
        
        //database access
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(p("dataSource.JdbcUrl"));
        config.setUsername(p("dataSource.setUsername"));
        config.setPassword(p("dataSource.setPassword"));
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
                                
        dataSource = new HikariDataSource(config);
    }
}

class StopHandler extends ContextHandler {

    public StopHandler() {
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Basis http response
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK); //200
        response.getWriter().println("<h1>Stopped</h1>");
        baseRequest.setHandled(true);

        new Thread() {
            @Override
            public void run() {
                try {
                    JettyServer.internalStop();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }
}
