/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controllers;

/**
 *
 * @author mlarr
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.javalite.activejdbc.Model;
import org.javalite.activeweb.AppController;

public class ControllerGenerico extends AppController {

    List<Model> getModelFromParameter(String param, Class c) {

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
                Model m = (Model) c.newInstance();
                m.fromMap(entrada);
                lista.add(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return lista;

    }

}
