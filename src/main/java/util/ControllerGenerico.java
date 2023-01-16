package util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.javalite.activeweb.AppController;

/**

 @author miguel
 */
public abstract class ControllerGenerico extends AppController {

    private void respondJsonInternal(String s) {
        respond(s).contentType("text/json")
                .header("Access-Control-Allow-Origin", "*")
                .status(200);
    }

    protected void respondJson(Object objeto) {

        respondJsonInternal(UtilConverter.toJson(objeto));
    }


    protected void respondOk(String... objeto) {

        String resultado = objeto[0];

        for (int i = 1; i < objeto.length; i++) {
            resultado = resultado + "," + objeto[i];
        }

        respond(resultado).status(200);

    }
    protected void respondOk(Integer i) {

        respondOk(i.toString());

    }
    protected void respondError(String... objeto) {
        String resultado = objeto[0];

        for (int i = 1; i < objeto.length; i++) {
            resultado = resultado + "," + objeto[i];
        }

        respond(resultado).status(500);

    }

    protected <T extends ModeloGenerico> List<T> getModelFromParameter(String param, Class<T> c) {
        Map<String, List<String>> collect = null;
        try {
            collect = params().entrySet().stream()
                    .filter(x -> x.getKey().startsWith(param + "["))
                    .collect(Collectors.toMap(x -> x.getKey().replaceFirst(param + "\\[", "").replaceFirst("\\]", ""), x -> Arrays.asList(x.getValue())));
        } catch (Exception e) {
            collect = params().entrySet().stream()
                    .filter(x -> x.getKey().startsWith(param + "["))
                    .collect(Collectors.toMap(x -> x.getKey().replaceFirst(param + "\\[", "").replaceFirst("\\]", ""), x -> Arrays.asList(x.getValue())));
        }
        Object[] keySet = (Object[]) collect.keySet().toArray();

        int size = collect.get(keySet[0]).size();

        Map<String, String> entrada = new HashMap();

        List<T> lista = new ArrayList<T>();

        for (int i = 0; i < size; i++) {
            for (Object k : keySet) {
                entrada.put((String) k, collect.get(k).get(i));
            }
            try {
                T m = c.getDeclaredConstructor().newInstance();
                m.fromMap(entrada);
                lista.add(m);
            } catch (Exception e) {
                logError(e);
            }
        }

        return lista;

    }

    protected Subject sessionShiro() {

        return SecurityUtils.getSubject();

    }

}
