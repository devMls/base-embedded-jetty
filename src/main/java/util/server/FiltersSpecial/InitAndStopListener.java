package util.server.FiltersSpecial;

import app.config.DbConfig;
import java.io.File;
import java.io.FileInputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.javalite.app_config.AppConfig;
import util.server.JettyServer;

/**
 *
 * @author mlarr
 */
public class InitAndStopListener implements ServletContextListener {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InitAndStopListener.class);

    public void contextInitialized(ServletContextEvent event) {

        try {
            String relativeWARPath = "/META-INF/MANIFEST.MF";
            String absoluteDiskPath = event.getServletContext().getRealPath(relativeWARPath);
            File file = new File(absoluteDiskPath);

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                String version = (String) new Manifest(fileInputStream)
                        .getMainAttributes()
                        .get(Attributes.Name.IMPLEMENTATION_VERSION);

                AppConfig.setProperty(Attributes.Name.IMPLEMENTATION_VERSION.toString(), version);
            } catch (Exception ex) {
                AppConfig.setProperty(Attributes.Name.IMPLEMENTATION_VERSION.toString(), "NO_MANIFEST");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            AppConfig.setProperty(Attributes.Name.IMPLEMENTATION_VERSION.toString(), ex.getMessage());

        }

    }

    public void contextDestroyed(ServletContextEvent event) {

        if (JettyServer.dataSource != null && !JettyServer.dataSource.isClosed()) {

            JettyServer.dataSource.close();

        }
    }
}
