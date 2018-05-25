package hx.data.mybatis.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

/**
 * @Author mingliang
 * @Date 2017-09-21 20:04
 */
public class BaseMapperUpdateListProvider extends MapperTemplate {

    public BaseMapperUpdateListProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String updateListDynamicConditional(MappedStatement ms){
        return "";
    }

}
