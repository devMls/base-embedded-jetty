package app.config;

import com.jagregory.shiro.freemarker.ShiroTags;
import freemarker.template.TemplateModelException;
import java.util.jar.Attributes;
import static org.javalite.app_config.AppConfig.p;

public class FreeMarkerConfig extends org.javalite.activeweb.freemarker.AbstractFreeMarkerConfig {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FreeMarkerConfig.class);

    @Override
    public void init() {
        //this is to override a strange FreeMarker default processing of numbers 
        getConfiguration().setNumberFormat("0.##");

        getConfiguration().setSharedVariable("shiro", new ShiroTags());

        try {

            getConfiguration().setSharedVariable("VERSIONWAR", p(Attributes.Name.IMPLEMENTATION_VERSION.toString()));

        } catch (Exception ex) {
            try {
                log.error(ex.getMessage(), ex);
                getConfiguration().setSharedVariable("VERSIONWAR", "ERROR: " + ex.getMessage());
            } catch (TemplateModelException ex1) {
                log.error(ex1.getMessage(), ex);

            }
        }

    }

}
