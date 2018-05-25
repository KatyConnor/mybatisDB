package hx.data.mybatis.provider;

import hx.data.mybatis.entity.WhereSqlEntity;
import hx.data.mybatis.utils.ParamHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.*;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static tk.mybatis.mapper.util.MsUtil.getMapperClass;

/**
 *  单表动态条件查询
 * @Author mingliang
 * @Date 2017-09-13 14:47
 */
public class BaseMapperSelectProvider  extends MapperTemplate {

    private Class<?> mapperClass;

    public BaseMapperSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
        this.mapperClass = mapperClass;
    }

    /**
     *  mapper 查询方法
     * @param ms
     * @return
     */
    public SqlNode findDynamicConditional(MappedStatement ms){

        // 获取返回类型
        Class<?> entityClass = getEntityClass(ms);
        // 获取入参
        Class<?> paramClass = ParamHelper.getParamClass(ms,this.mapperClass);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        List<SqlNode> sqlNodes = new ArrayList<>();
        Map<String,EntityColumn> columnMap = new HashMap<>();
        ParamHelper.getTableColumns(entityClass,columnMap);

        //获取Mapper方法的查询参数
        WhereSqlEntity whereSqlEntity = ParamHelper.resolveParameter(paramClass,columnMap);

        //静态的sql部分:select column ... from table
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(SqlHelper.getAllColumns(entityClass)).append(" FROM ").
                append(tableName(entityClass));

        sqlNodes.add(new TextSqlNode(sql.toString()));
        //将if添加到<where>
        sqlNodes.add(new WhereSqlNode(ms.getConfiguration(), whereSqlEntity.getTrimSqlNode()));
//        sqlNodes.add(new WhereSqlNode(ms.getConfiguration(), new MixedSqlNode(whereSqlEntity.getIfNodes())));
        // 将group和orderby加上
        if (StringUtils.isNotBlank(whereSqlEntity.getGroupBy())){
            sqlNodes.add(new TextSqlNode(whereSqlEntity.getGroupBy()));
        }
        if (StringUtils.isNotBlank(whereSqlEntity.getOrderBy())){
            sqlNodes.add(new TextSqlNode(whereSqlEntity.getOrderBy()));
        }

        return new MixedSqlNode(sqlNodes);
    }

    /**
     * 分页查询
     * @param ms
     * @return
     */
    public SqlNode findDynamicConditionalByPage(MappedStatement ms){
        return findDynamicConditional(ms);
    }

    /**
     *  获取泛型查询参数
     * @param ms
     * @return
     */
//    private Class<?> getParamClass(MappedStatement ms) {
//        String msId = ms.getId();
//        if (this.paramClassMap.containsKey(msId)) {
//            return (Class<?>)this.paramClassMap.get(msId);
//        } else {
//            Class<?> mapperClass = getMapperClass(msId);
//            Type[] types = mapperClass.getGenericInterfaces();
//            Type[] var5 = types;
//            int var6 = types.length;
//
//            for(int var7 = 0; var7 < var6; ++var7) {
//                Type type = var5[var7];
//                if (type instanceof ParameterizedType) {
//                    ParameterizedType t = (ParameterizedType)type;
//                    if (t.getRawType() == this.mapperClass || this.mapperClass.isAssignableFrom((Class)t.getRawType())) {
//                        Class<?> returnType = (Class)t.getActualTypeArguments()[1];
//                        return returnType;
//                    }
//                }
//            }
//            throw new MapperException(String.format("获取参数对象属性失败 %s",msId));
//        }
//    }
}
