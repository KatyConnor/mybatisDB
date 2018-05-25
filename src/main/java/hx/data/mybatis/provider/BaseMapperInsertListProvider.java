package hx.data.mybatis.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

/**
 * @Author mingliang
 * @Date 2017-09-21 21:30
 */
public class BaseMapperInsertListProvider  extends MapperTemplate {

    public BaseMapperInsertListProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String insertLists(MappedStatement ms){
        return "";
    }
}
