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
import java.util.Map;

/**

 @author mlarr
 */
public class UtilConverter {

    //    public static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
    private static final ObjectMapper mapper = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz")); // jackson's objectmapper;

    public static Object fromMap(Map map, Class c) {

        return mapper.convertValue(map, c);
    }

    public static Map toMap(Object o) {

        return mapper.convertValue(o, new TypeReference<Map<String, Object>>() {
        });
    }

    public static String toJson(Object o) {

        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            return null;
        }

    }

    public static Object fromJson(String json, Class c) {
        try {
            return mapper.readValue(json, c);
        } catch (Exception ex) {
            return null;
        }

    }

}
