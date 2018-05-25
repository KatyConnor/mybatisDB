package hx.data.mybatis.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

/**
 * @Author mingliang
 * @Date 2017-09-21 20:41
 */
public class BaseMapperDeleteProvider extends MapperTemplate {

    public BaseMapperDeleteProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String deleteDynamicConditional(MappedStatement ms){
        return "";
    }
}
