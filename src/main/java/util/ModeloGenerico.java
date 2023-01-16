/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.javalite.activejdbc.ColumnMetadata;
import org.javalite.activejdbc.Model;

/**

 @author mlarr
 */
//esto se podra poner como no serializable hacia arriba
@JsonIgnoreProperties({"index", "dbPropertyA", "dbPropertyB"})
public abstract class ModeloGenerico extends Model {

    public void fromObject(Object o) {

        fromMap(UtilConverter.toMap(o));

    }

    public <T> T toObject(Class<T> c) {

        return UtilConverter.fromMap(toMap(), c);

    }

    public static List<String> getNombreColumnas(String... exclusiones) {

        ArrayList<String> names = new ArrayList<String>();

        Map<String, ColumnMetadata> columnMetadata = getMetaModel().getColumnMetadata();

        Set<String> keySet = columnMetadata.keySet();

        keySet.stream().forEach(key -> names.add(columnMetadata.get(key).getColumnName().toLowerCase()));

        names.removeAll(Arrays.asList(exclusiones));

        return names;

    }

}
