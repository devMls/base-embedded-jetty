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
import org.javalite.activejdbc.Model;
import org.javalite.activeweb.AppController;

/**
 *
 * @author miguel
 */
public class ControllerGenerico extends AppController {

    public void respondJson(Object objeto) {

        respond(UtilConverter.toJson(objeto)).contentType("text/json")
                .header("Access-Control-Allow-Origin", "*")
                .status(200);
    }

    public List<Model> getModelFromParameter(String param, Class c) {

        Map<String, List<String>> collect = params().entrySet().stream()
                .filter(x -> x.getKey().startsWith(param + "."))
                .collect(Collectors.toMap(x -> x.getKey().replaceFirst(param + ".", ""), x -> Arrays.asList(x.getValue())));

        String[] keySet = (String[]) collect.keySet().toArray();

        int size = collect.get(keySet[0]).size();

        Map<String, String> entrada = new HashMap();

        List<Model> lista = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            for (String k : keySet) {
                entrada.put(k, collect.get(k).get(i));
            }
            try {
                Model m = (Model) c.getDeclaredConstructor().newInstance();
                m.fromMap(entrada);
                lista.add(m);
            } catch (Exception e) {
                logError(e);
            }
        }

        return lista;

    }

    public Subject sessionShiro() {

        return SecurityUtils.getSubject();

    }

}
