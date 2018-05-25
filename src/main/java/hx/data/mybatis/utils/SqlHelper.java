package hx.data.mybatis.utils;

import hx.data.mybatis.annotation.Like;
import hx.data.mybatis.annotation.NotLike;
import hx.data.mybatis.entity.ColumnEntity;
import hx.data.mybatis.entity.FieldEntity;
import hx.data.mybatis.entity.TableEntity;
import hx.data.mybatis.entity.WhereSqlEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.EntityColumn;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * sql组装
 * @Author mingliang
 * @Date 2017-11-23 14:29
 */
public class SqlHelper {

    private static final String BETWEEN = "BETWEEN";
    private static final String AND = "AND";
    private static final String CONNECT_AND = " AND ";
    private static final String LIKE = "LIKE";
    private static final String NOT_LIKE = "NOT LIKE";
    private static final String NOT = "NOT";
    private static final String PARAM_START = " #{";
    private static final String PARAM_END = "}";
    private static final String EQUAL = " = ";
    private static final String IN = "IN";
    private static final String NOT_IN = "NOT IN";


    /**
     * 组装sql,组装sql后面的Where 条件部分，结构如下，<where><trim> <if></if> ....  </trim></where>
     * 将合并完之后的结构存储在 TableEntity 对象
     * @param columnMap 数据库列
     */
    public static WhereSqlEntity analyticSQL(TableEntity tableEntity,Map<String, EntityColumn> columnMap){
        WhereSqlEntity whereSqlEntity = new WhereSqlEntity();
        Map<String, ColumnEntity> columnEntityMap = tableEntity.getColumnEntity();
        if (CollectionUtils.isEmpty(columnEntityMap)){
            return whereSqlEntity;
        }

        for (Map.Entry entry : columnEntityMap.entrySet()){
            ColumnEntity columnEntity = (ColumnEntity) entry.getValue();
            Map<String, FieldEntity> fieldEntityMap = columnEntity.getFieldEntity();
            if (CollectionUtils.isEmpty(fieldEntityMap)){
                continue;
            }

            // 检查是否包含between 或者 notbetween
            checkBetween(fieldEntityMap);

            for (Map.Entry entry1 : fieldEntityMap.entrySet()){
                FieldEntity fieldEntity = (FieldEntity) entry1.getValue();
                String fieldName = fieldEntity.getFieldName();
                String fieldStart = fieldEntity.getFieldNameStart();
                String fieldEnd = fieldEntity.getFieldNameEnd();

                List<SqlNode> ifNodes = new ArrayList<>();
                EntityColumn column = getEntityColumn(fieldEntity.getColumn(),fieldName,columnMap);
                StringBuilder sqlBuilder = new StringBuilder();

                // 拼装sql
                assemblingSql(fieldEntity,sqlBuilder,column,whereSqlEntity);
                StaticTextSqlNode columnNode = new StaticTextSqlNode(sqlBuilder.toString());

                // 如果参数为字符串需要验证入参不能为空或者空字符串
                StringBuilder test = new StringBuilder();
                if (column.getJavaType().equals(String.class)) {
                    test.append(fieldName !=null?fieldName+ " != null and " + fieldName + ".trim() !=\'\'":"");
                    test.append(fieldStart !=null?fieldStart+ " != null and !" + fieldStart + ".trim() !=\'\'":"");
                    test.append(fieldEnd !=null?" and "+fieldEnd+ " != null and !" + fieldEnd + ".trim() !=\'\'":"");
                    ifNodes.add(new IfSqlNode(columnNode, test.toString()));
                } else {
                    test.append(fieldName !=null?fieldName+ " != null":"");
                    test.append(fieldStart !=null?fieldStart+ " != null ":"");
                    test.append(fieldEnd !=null?" and "+fieldEnd+ " != null":"");
                    ifNodes.add(new IfSqlNode(columnNode, test.toString()));
                }
                // 分组或者排序操作
                if (fieldEntity.isGroupBy()){
                    whereSqlEntity.setGroupBy(String.format(" GROUP BY %s",column.getColumn()));
                }
                if (fieldEntity.isOrderBy()){
                    whereSqlEntity.setOrderBy(String.format(" ORDER BY %s %s",column.getColumn(),fieldEntity.getOrderType()));
                }
                whereSqlEntity.setIfNodes(ifNodes);

            }
        }
        TrimSqlNode trimSqlNode = new TrimSqlNode(new Configuration(),new MixedSqlNode(whereSqlEntity.getIfNodes()),
                null,null,null,AND);
        whereSqlEntity.setTrimSqlNode(trimSqlNode);
        return whereSqlEntity;
    }

    /**
     *
     * @param fieldEntityMap
     */
    private static void checkBetween(Map<String, FieldEntity> fieldEntityMap){
        FieldEntity fieldEntityStart = null;
        FieldEntity fieldEntityEnd = null;
        boolean startFlag = false;
        boolean endFlag = false;
        for (Map.Entry entry : fieldEntityMap.entrySet()){
            FieldEntity entity = (FieldEntity) entry.getValue();
            // 包含有between查询必须是并行的
            if (StringUtils.isNotBlank(entity.getStart()) && entity.getStart().indexOf(BETWEEN) != -1){
                startFlag = true;
                fieldEntityStart = entity;
            }

            if (StringUtils.isNotBlank(entity.getEnd()) && entity.getEnd().indexOf(AND) != -1){
                endFlag = true;
                fieldEntityEnd = entity;
            }

            if (startFlag && endFlag){
                break;
            }
        }

        if (startFlag && endFlag){
            checkStartAndEndField(fieldEntityStart,fieldEntityEnd);
            FieldEntity fieldEntity = new FieldEntity();
            BeanUtils.copyProperties(fieldEntityStart,fieldEntity);
            fieldEntity.setEnd(fieldEntityEnd.getEnd());
            fieldEntity.setFieldNameEnd(fieldEntityEnd.getFieldNameEnd());
            fieldEntityMap.put(fieldEntity.getFieldNameStart(),fieldEntity);
            fieldEntityMap.remove(fieldEntityEnd.getFieldNameEnd());
        }

    }

    /**
     * between注解验证,是否 between and ， not between and 联合使用，如果没有抛出异常
     * @param start between ，not between注解
     * @param end  and
     */
    private static void checkStartAndEndField(FieldEntity start,FieldEntity end){
        if (start == null || end == null){
            throw new IllegalArgumentException(" between 查询必须联合使用！");
        }
    }

    /**
     *
     * @param contents
     * @return
     */
    private static MixedSqlNode mixedContents(SqlNode... contents) {
        return new MixedSqlNode(Arrays.asList(contents));
    }

    /**
     *
     * @param sqlNodes
     * @return
     */
    private static MixedSqlNode mixedContents(List<SqlNode> sqlNodes) {
        return new MixedSqlNode(sqlNodes);
    }

    /**
     * 根据入参制定的数据库查询映射字段获取 EntityColumn 对象，如果没有指定默认取入参字段匹配
     * @param cloumnName 指定映射字段
     * @param fieldName 入参字段
     * @param columnMap 表对应的所有列
     * @return 返回 EntityColumn 对象
     */
    private static EntityColumn getEntityColumn(String cloumnName,String fieldName,Map<String, EntityColumn> columnMap){
        EntityColumn column = null;
        if (StringUtils.isNotBlank(cloumnName)){
            column = columnMap.get(cloumnName);
        }
        if (column == null){
            column = columnMap.get(fieldName);
        }
        if (column == null){
            throw new IllegalArgumentException(String.format("参数字段需要必备的查询注解并指明映射数据库字段或者保持字段和" +
                    "数据库表字段一直，下划线以大小写代替！field = [%s], cloumn = [%s]",fieldName,cloumnName));
        }
        return column;
    }

    /**
     *  组装sql，存入StringBuilder 中
     * @param fieldEntity
     * @param sqlBuilder
     * @param column
     * @param whereSqlEntity
     * @return
     */
    private static boolean assemblingSql(FieldEntity fieldEntity,StringBuilder sqlBuilder,EntityColumn column,WhereSqlEntity whereSqlEntity){
        Annotation annotation = fieldEntity.getAnnotation();
        String type = fieldEntity.getType();
        // 在区间查询时设置type为null，进行区间sql的拼装
        if (type == null){
            assemblingSectionSql(fieldEntity,sqlBuilder,column);
        }else if (type != null){
            // 其他非区间查询字段
            // 模糊匹配
            if (type.indexOf(LIKE) != -1){
                if (type.indexOf(NOT_LIKE) != -1){
                    NotLike like = (NotLike) annotation;
                    String before = like.before();
                    String after = like.after();
                    String around = like.around();
                    assemblingLikeSql(before,after,around,type,fieldEntity.getFieldName(),sqlBuilder,column);
                }else {
                    Like like = (Like) annotation;
                    String before = like.before();
                    String after = like.after();
                    String around = like.around();
                    assemblingLikeSql(before,after,around,type,fieldEntity.getFieldName(),sqlBuilder,column);
                }
            }else if (type.indexOf(IN) != -1){
                assemblingForEachSql(type,fieldEntity,whereSqlEntity,column);
                return true;
            } else {
                // 等号精确匹配
                sqlBuilder.append(column.getColumn()).append(type == null?EQUAL:type).
                        append(PARAM_START).append(fieldEntity.getFieldName()).append(PARAM_END);
            }
        }
        sqlBuilder.append(CONNECT_AND);
        return false;
    }

    /**
     * 组装区间条件查询字段，如 between and 查询， not between and 查询， > < >= <= 查询sql拼装
     * @param fieldEntity
     * @param sqlBuilder
     * @param column
     */
    private static void assemblingSectionSql(FieldEntity fieldEntity,StringBuilder sqlBuilder,EntityColumn column){
        if ( fieldEntity.getStart() != null && fieldEntity.getEnd() != null){
            sqlBuilder.append(column.getColumn()).append(fieldEntity.getStart()).append(PARAM_START).
                    append(fieldEntity.getFieldNameStart()).
                    append(PARAM_END).append(fieldEntity.getEnd()).append(PARAM_START).
                    append(fieldEntity.getFieldNameEnd()).append(PARAM_END);
        }else {
            if (StringUtils.isNotBlank(fieldEntity.getStart())){
                sqlBuilder.append(column.getColumn()).append(fieldEntity.getStart()).append(PARAM_START).
                        append(fieldEntity.getFieldNameStart()).append(PARAM_END);
            }
            if (StringUtils.isNotBlank(fieldEntity.getEnd())){
                sqlBuilder.append(column.getColumn()).append(fieldEntity.getEnd()).append(PARAM_START).
                        append(fieldEntity.getFieldNameEnd()).append(PARAM_END);
            }
        }
    }

    /**
     * 组装模糊匹配的字段
     * @param before
     * @param after
     * @param around
     * @param type
     * @param fieldName
     * @param sqlBuilder
     * @param column
     */
    private static void assemblingLikeSql(String before,String after,String around,String type,
                                          String fieldName,StringBuilder sqlBuilder,EntityColumn column){
        if ((StringUtils.isNotBlank(before) && StringUtils.isNotBlank(after)) || StringUtils.isNotBlank(around)) {
            sqlBuilder.append(column.getColumn()).append(type).append(" CONCAT('%',#{"+fieldName+"}, '%')");
        }

        if (StringUtils.isNotBlank(before)) {
            sqlBuilder.append(column.getColumn()).append(type).append(" CONCAT('%',#{"+fieldName+"})");
        }

        if (StringUtils.isNotBlank(after)) {
            sqlBuilder.append(column.getColumn()).append(type).append(" CONCAT(#{"+fieldName+"}, '%')");
        }
    }

    /**
     * 组装一个字段多条件匹配
     * @param type
     * @param fieldEntity
     * @param whereSqlEntity
     * @param column
     */
    private static void assemblingForEachSql(String type,FieldEntity fieldEntity,WhereSqlEntity whereSqlEntity,EntityColumn column) {
        // 循环多条件
        List<SqlNode> ifNodes = new ArrayList<>();
        StringBuilder open = new StringBuilder();
        open.append(column.getColumn()).append(type.indexOf(NOT_IN) != -1?" NOT IN (":" IN (");
        ForEachSqlNode forEachSqlNode = new ForEachSqlNode(new Configuration(),mixedContents(new TextSqlNode("#{item}")),
                fieldEntity.getFieldName(),null,"item", open.toString(),")",",");
        ifNodes.add(new IfSqlNode(forEachSqlNode, fieldEntity.getFieldName()+ " != null"));
        whereSqlEntity.setIfNodes(ifNodes);
    }
}
