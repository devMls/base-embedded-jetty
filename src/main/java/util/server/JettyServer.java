package util.server;

import app.config.DbConfig;
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
import java.net.InetAddress;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import util.server.FiltersSpecial.InitAndStopListener;

public class JettyServer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JettyServer.class);

    private static org.eclipse.jetty.server.Server server;
    public static HikariDataSource dataSource;

    public static int TIME_TO_STOP = 3000;

    public static void main(String[] args) throws Exception {

        if (args.length > 0 && args[0].equalsIgnoreCase("stop")) {
            stop();
        } else if (args.length > 0 && args[0].equalsIgnoreCase("restart")) {
            restart();
        } else {
            start();
        }
    }

    private static void restart() {
        stop();
        try {
            Thread.sleep(TIME_TO_STOP + 2000);
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
        }
        start();

    }

    private static void stop() {

        try {
            log.info("Stopping....");
            String urlString = "http://localhost:" + p("stopPort") + "/stop";
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            is.close();
            log.info("Stopped");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

    }

    private static void start() {

        server = new org.eclipse.jetty.server.Server();

        dataSource = DbConfig.generateDataSource();

        createConnectors();

        WebAppContext appHandler = crateAppHandler();

        SessionHandler sessionHandler = createSessionHandler(dataSource);

        appHandler.setSessionHandler(sessionHandler);

        //stop Handler
        StopHandler stopHandler = new StopHandler();
        stopHandler.setVirtualHosts(new String[]{"@stopConnector"});
        stopHandler.setContextPath("/stop");

        HandlerCollection collection = new HandlerCollection(appHandler, stopHandler);

        StatisticsHandler statsHandler = new StatisticsHandler();
        statsHandler.setHandler(collection);

        server.setHandler(statsHandler);
        server.setStopTimeout(TIME_TO_STOP);
        server.setStopAtShutdown(true);

        try {
            server.start();
            server.dumpStdErr();
            server.join();
            log.info("finally main");
        } catch (Exception t) {
            log.error("error inicializando servidor", t);
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

    private static SessionHandler createSessionHandler(HikariDataSource d) {
        //session in database
        final DefaultSessionIdManager idmgr = new DefaultSessionIdManager(server);
        idmgr.setServer(server);
        try {
            idmgr.setWorkerName(InetAddress.getLocalHost().getHostAddress().replace(".", "_"));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        server.setSessionIdManager(idmgr);
        JDBCSessionDataStore sessionStore = new JDBCSessionDataStore();
        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDatasource(d);
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
        appHandler.setResourceBase("src/main/webapp");
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
                log.info("finalled");
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }

        }
    }

}

class StopHandler extends ContextHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StopHandler.class);

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
                    log.error(ex.getMessage(), ex);
                }
            }
        }.start();
    }
}
