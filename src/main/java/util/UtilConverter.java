/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dozer.DozerBeanMapper;
import org.javalite.activejdbc.ColumnMetadata;
import org.javalite.activejdbc.MetaModel;

/**

 @author mlarr
 */
public class UtilConverter {

    //    public static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
    private static final ObjectMapper JSONmapper = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz")); // jackson's objectmapper;
    private static final DozerBeanMapper DozerMapper = new DozerBeanMapper();

    public static <T> T fromMap(Map map, Class<T> c) {

        return JSONmapper.convertValue(map, c);
    }

    public static Map toMap(Object json) {

        return JSONmapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public static String toJson(Object o) {

        try {
            return JSONmapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            return null;
        }

    }

    public static <T> T fromJson(String json, Class<T> c) {
        try {
            return JSONmapper.readValue(json, c);
        } catch (Exception ex) {
            return null;
        }

    }

    public static List<String> getNombreColumnas(MetaModel metaModel, String... exclusiones) {

        ArrayList<String> names = new ArrayList<String>();

        Map<String, ColumnMetadata> columnMetadata = metaModel.getColumnMetadata();

        Set<String> keySet = columnMetadata.keySet();

        String name = null;

        names.add("id");

        for (String k : keySet) {
            name = columnMetadata.get(k).getColumnName().toLowerCase();
            if (!name.contains("_id") && !name.equals("id")) {
                names.add(name);
            }
        }

        if (exclusiones != null) {
            names.removeAll(Arrays.asList(exclusiones));
        }

        return names;

    }

//    public static Object fromModel(Model m, Class c) {
//
//        return JSONmapper.convertValue(m.toJson(false), c);
//
//    }
//
//    public static Model toModel(Class c, Object o) {
//        try {
//            Model m = (Model) c.getDeclaredConstructor().newInstance();
//
//            return m.fromMap(toMap(o));
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
