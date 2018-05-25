package hx.data.mybatis.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库表和实体映射处理工具类
 * @Author mingliang
 * @Date 2017-11-24 14:19
 */
public class TableFieldUtils {


    /**
     * 将对象的大写转换为下划线加小写，例如：userName-->user_name
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toUnderlineJSONString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String reqJson = mapper.writeValueAsString(object);
        return reqJson;
    }

    /**
     * 将下划线转换为驼峰的形式，例如：user_name-->userName
     *
     * @param json
     * @param clazz
     * @return
     * @throws IOException
     */
     public static <T> T toSnakeObject(String json, Class<T> clazz) throws IOException {
         ObjectMapper mapper = new ObjectMapper();
         mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
         T reqJson =  mapper.readValue(json, clazz);
         return reqJson;
     }

}
