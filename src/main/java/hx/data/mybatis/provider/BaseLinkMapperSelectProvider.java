package hx.data.mybatis.provider;

import hx.data.mybatis.entity.WhereSqlEntity;
import hx.data.mybatis.utils.ParamHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.scripting.xmltags.WhereSqlNode;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author mingliang
 * @Date 2017-11-24 14:07
 */
public class BaseLinkMapperSelectProvider extends MapperTemplate {

    private Class<?> mapperClass;

    public BaseLinkMapperSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
        this.mapperClass = mapperClass;
    }

    /**
     *  mapper 查询方法
     * @param ms
     * @return
     */
    public SqlNode findMultiTable(MappedStatement ms){
        // 获取返回类型
        Class<?> entityClass = getEntityClass(ms);
        // 获取入参
        Class<?> paramClass = ParamHelper.getParamClass(ms,mapperClass);
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

}
