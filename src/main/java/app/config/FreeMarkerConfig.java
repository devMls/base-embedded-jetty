package app.config;

import freemarker.template.TemplateModelException;

public class FreeMarkerConfig extends org.javalite.activeweb.freemarker.AbstractFreeMarkerConfig {
    @Override
    public void init() {
        //this is to override a strange FreeMarker default processing of numbers 
        getConfiguration().setNumberFormat("0.##");
        
        try {
            getConfiguration().setSharedVariable("VERSIONWAR","00");
        } catch (TemplateModelException ex) {
            try {
                getConfiguration().setSharedVariable("VERSIONWAR","ERROR");
            } catch (TemplateModelException ex1) {
                
            }
        }

        
        //EN VEZ DE USAR EL MODO A MANO USAR MessageTAG i18n javalite
        //impode que si falta una variable pete pero hay que poner la ruta entera en los include
     //   getConfiguration().setClassicCompatible(true);

      //  activarI18nConDefaultLocal("es_ES");
            
    }

//    private void activarI18nConDefaultLocal(String defaultLocale) {
//        getConfiguration().setSharedVariable("i18nfreemarker", new I18NTemplateHashModel(defaultLocale));
//        
//   //     intenta añadir aunto include         getConfiguration().add
//
//    }
}