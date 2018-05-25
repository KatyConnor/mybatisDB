package hx.data.mybatis.utils;

import hx.data.mybatis.annotation.*;
import hx.data.mybatis.commonenum.MapperSelectConditionStaticConstantEnum;
import hx.data.mybatis.entity.ColumnEntity;
import hx.data.mybatis.entity.FieldEntity;
import hx.data.mybatis.entity.TableEntity;
import hx.data.mybatis.entity.WhereSqlEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static hx.data.mybatis.commonenum.MapperSelectConditionStaticConstantEnum.*;
import static tk.mybatis.mapper.util.MsUtil.getMapperClass;

/**
 * 参数解析
 * @Author mingliang
 * @Date 2017-11-23 14:31
 */
public class ParamHelper {

    protected static Map<String, Class<?>> paramClassMap = new ConcurrentHashMap();

    /**
     * 解析参数实体
     * @param paramClass
     * @param columnMap
     * @return
     */
    public static WhereSqlEntity resolveParameter(Class<?> paramClass,Map<String,EntityColumn> columnMap){
        WhereSqlEntity whereSqlEntity = null;
        try {
            // 获取参数实体的所有参数字段
            Field[] fields = paramClass.getDeclaredFields();
            if (fields.length <=0){
                return new WhereSqlEntity();
            }
            // 参数检验和解析
            TableEntity tableEntity = paramCheckAndAnalysis(fields,columnMap);

            // 拼装sql
            if (!CollectionUtils.isEmpty(tableEntity.getColumnEntity())){
                // 解析每个字段的注解然后组装成sql
                whereSqlEntity = SqlHelper.analyticSQL(tableEntity,columnMap);
            }
        } catch (Exception e) {
            throw new MapperException(e.getMessage());
        }
        return whereSqlEntity;
    }

    /**
     * 获取当前表的所有列字段，需要查询的列字段
     * @param entityClass
     * @param columnMap
     */
    public static void getTableColumns(Class<?> entityClass,Map<String,EntityColumn> columnMap){
        //获取全部列，列中的属性propertity的是数据库字段驼峰命名结果
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnSet) {
            columnMap.put(column.getProperty(),column);
        }
    }


    /**
     * 参数合法性验证
     * @param fields
     * @param columnMap
     */
    private static TableEntity paramCheckAndAnalysis(Field[] fields,Map<String,EntityColumn> columnMap){
        TableEntity tableEntity = new TableEntity();
        Map<String,ColumnEntity> columnEntityMap = new HashMap<>();

        for ( int i = 0; i < fields.length; i++ ) {
            Field field = fields[i];
            Annotation[] fieldAnnotation = field.getDeclaredAnnotations();
            // 如果该字段没有加查询注解，默认取字段映射表中的字段
            if (fieldAnnotation.length <= 0 && columnMap.get(field.getName()) != null){
                String column = field.getName();
                ColumnEntity columnEntity = columnEntityMap.get(column);
                if (columnEntity == null){
                    columnEntity = new ColumnEntity();
                }
                Map<String,FieldEntity> fieldEntityMap = getFieldEntity(columnEntity);

                FieldEntity fieldEntity = new FieldEntity();
                fieldEntity.setColumn(column);
                fieldEntity.setFieldName(field.getName());
                fieldEntity.setField(field);
                fieldEntity.setType(MapperSelectConditionStaticConstantEnum.EQUAL_TO.getMessage());
                columnEntity.setColumn(column);
                fieldEntityMap.put(field.getName(),fieldEntity);
                columnEntity.setFieldEntity(fieldEntityMap);
                columnEntityMap.put(column,columnEntity);
            }else if (fieldAnnotation.length > 0){
                // 一个字段添加的查询注解只能一个
                int isHave = 0;
                // 如果字段添加了注解对注解进行解析
                StringBuilder annotations =  new StringBuilder();
                for (Annotation annotation : fieldAnnotation){
                    String annotationType = annotation.annotationType().getSimpleName();
                    isHave = check(annotationType)?++isHave:isHave;

                    annotations.append(annotationType);
                    if (isHave >= 2){
                        throw new IllegalArgumentException(String.format("每个属性字段查询条件注解只能存在一个！" +
                                "annotations = [ %s ]",annotations.toString()));
                    }
                    // 将参数按照注解解析验证，放到对象FieldEntity
                    analyticParamField(annotation,field,columnEntityMap);
                }
            }
        }

        tableEntity.setColumnEntity(columnEntityMap);
        return tableEntity;
    }

    /**
     *
     * @param annotation
     * @param field
     * @param columnEntityMap
     */
    private static void analyticParamField(Annotation annotation,Field field,
                                           Map<String,ColumnEntity> columnEntityMap) {
        String column = "";
        FieldEntity fieldEntity = null;
        ColumnEntity columnEntity = null;
        Map<String,FieldEntity> fieldEntityMap = null;
        String annotationType = annotation.annotationType().getSimpleName();
        switch (getByCode(annotationType)){
            case EQUAL_TO:
                column = getColumn(((EqualTo) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,EQUAL_TO,
                        false,false,null);
                break;
            case NOT_EQUAL_TO:
                column = getColumn(((NotEqualTo) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,NOT_EQUAL_TO,
                        false,false,null);
                break;

            case BETWEEN_START:
                column = getColumn(((BetweenStart) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,BETWEEN_START,
                        true,false,null);
                break;

            case BETWEEN_END:
                column = getColumn(((BetweenEnd) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,BETWEEN_END,
                        false,true,null);
                break;

            case NOT_BETWEEN_START:
                column = getColumn(((NotBetweenStart) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,NOT_BETWEEN_START,
                        true,false,null);
                break;

            case NOT_BETWEEN_END:
                column = getColumn(((NotBetweenEnd) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,NOT_BETWEEN_END,
                        false,true,null);
                break;
            case GREATER_THAN:
                column = getColumn(((GreaterThan) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,GREATER_THAN,
                        true,false,null);
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                column = getColumn(((GreaterThanOrEqualTo) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,GREATER_THAN_OR_EQUAL_TO,
                        true,false,null);
                break;
            case LESS_THAN:
                column = getColumn(((LessThan) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,LESS_THAN,
                        false,true,null);
                break;
            case LESS_THAN_OR_EQUAL_TO:
                column = getColumn(((LessThanOrEqualTo) annotation).column(),field.getName());
                checkColumn(column,annotationType);
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,LESS_THAN_OR_EQUAL_TO,
                        false,true,null);
                break;
            case IN:
                column = getColumn(((In) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,IN,
                        false,false,null);
                break;
            case NOT_IN:
                column = getColumn(((NotIn) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,NOT_IN,
                        false,false,null);
                break;
            case IS_NULL:
                column = getColumn(((IsNull) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,IS_NULL,
                        false,false,null);
                break;

            case IS_NOT_NULL:
                column = getColumn(((IsNotNull) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,IS_NOT_NULL,
                        false,false,null);
                break;
            case LIKE:
                column = getColumn(((Like) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,LIKE,
                        false,false,null);
                break;

            case NOT_LIKE:
                column = getColumn(((NotLike) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,false,false,NOT_LIKE,
                        false,false,null);
                break;

            case GROUP_BY:
                column = getColumn(((GroupBy) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                setFieldEntity(column,field,fieldEntity,annotation,true,false,GROUP_BY,
                        false,false,null);
                break;

            case ORDER_BY:
                column = getColumn(((OrderBy) annotation).column(),field.getName());
                columnEntity = getColumnEntity(column,columnEntityMap);
                fieldEntityMap = getFieldEntity(columnEntity);
                fieldEntity = init(field.getName(),fieldEntityMap);
                String orderType = StringUtils.isNotBlank(((OrderBy) annotation).value())?((OrderBy) annotation).value():
                        "DESC";
                setFieldEntity(column,field,fieldEntity,annotation,false,true,ORDER_BY,
                        false,false,orderType);
                break;
            case LINK_FIELD:
                break;

            default:
                throw new IllegalArgumentException(String.format("注解类型错误！ @%s",annotationType));
        }
        // 放回原位置
        fieldEntityMap.put(field.getName(),fieldEntity);
        columnEntity.setFieldEntity(fieldEntityMap);
        columnEntityMap.put(column,columnEntity);
    }

    /**
     *
     * @param fieldName
     * @param fieldEntityMap
     * @return
     */
    private static FieldEntity init(String fieldName,Map<String,FieldEntity> fieldEntityMap){
        FieldEntity fieldEntity = fieldEntityMap.get(fieldName);
        if (fieldEntity == null){
            fieldEntity = new FieldEntity();
        }
        return fieldEntity;
    }

    /**
     *
     * @param column
     * @param columnEntityMap
     * @return
     */
    private static ColumnEntity getColumnEntity(String column,Map<String,ColumnEntity> columnEntityMap){
        ColumnEntity columnEntity = columnEntityMap.get(column);
        return columnEntity == null?new ColumnEntity():columnEntity;
    }

    /**
     *
     * @param columnEntity
     * @return
     */
    private static Map<String,FieldEntity> getFieldEntity(ColumnEntity columnEntity){
        Map<String,FieldEntity> fieldEntityMap = columnEntity.getFieldEntity();
        return CollectionUtils.isEmpty(fieldEntityMap)?new HashMap<>():fieldEntityMap;
    }

    /**
     * 将带下划线的数据库字段改成驼峰命名
     * @param column
     * @param fieldName
     * @return
     */
    private static String getColumn(String column,String fieldName){
        if (StringUtils.isBlank(column)){
            column = fieldName;
        }
        return hx.data.mybatis.utils.StringUtils.convertUnderlineToHump(column);
    }

    /**
     * 设置解析的结果值 ，TODO：目前一个字段的查询注解只有一个，后期需要改进一个字段可能存在多个条件注解
     * @param fieldEntity
     * @param column
     * @param field
     * @param groupBy
     * @param orderBy
     * @param _enum
     * @param start
     * @param end
     * @param annotation
     * @param orderType
     */
    private static void setFieldEntity(String column,Field field,FieldEntity fieldEntity,
                                       Annotation annotation,Boolean groupBy,Boolean orderBy,
                                       MapperSelectConditionStaticConstantEnum _enum,
                                       Boolean start,Boolean end,String orderType){
        if (StringUtils.isNotBlank(fieldEntity.getColumn()) && !fieldEntity.getColumn().equals(column)){
            throw new IllegalArgumentException(String.format("同一个字段不能指定多个列！column=[ %s,%s ] field = [ %s ],",
                    column,fieldEntity.getColumn(),field.getName()));
        }
        String annotationType = annotation.annotationType().getSimpleName();
        fieldEntity.setColumn(column);
        fieldEntity.setFieldName(!start && !end?field.getName(): start || end?
                null:fieldEntity.getFieldName());
        fieldEntity.setField(field);
        fieldEntity.setOrderBy(fieldEntity.isOrderBy()?fieldEntity.isOrderBy():orderBy);
        fieldEntity.setOrderType(StringUtils.isNotBlank(fieldEntity.getOrderType())?fieldEntity.getOrderType():orderType);
        fieldEntity.setGroupBy(fieldEntity.isGroupBy()?fieldEntity.isGroupBy():groupBy);
        fieldEntity.setStart(start?_enum.getMessage():StringUtils.isEmpty(fieldEntity.getStart())?
                null:fieldEntity.getStart());
        fieldEntity.setEnd(end?_enum.getMessage():StringUtils.isBlank(fieldEntity.getEnd())?
                null:fieldEntity.getEnd());
        fieldEntity.setFieldNameStart(start?field.getName():StringUtils.isBlank(fieldEntity.getFieldNameStart())?
                null:fieldEntity.getFieldNameStart());
        fieldEntity.setFieldNameEnd(end?field.getName():StringUtils.isBlank(fieldEntity.getFieldNameEnd())?
                null:fieldEntity.getFieldNameEnd());
        fieldEntity.setType(!start && !end && !groupBy && !orderBy?_enum.getMessage():
                StringUtils.isEmpty(fieldEntity.getType())?
                null:fieldEntity.getType());
        fieldEntity.setAnnotation(!"GroupBy".equals(annotationType) && !"OrderBy".equals(annotationType)?
                annotation:fieldEntity.getAnnotation());
        fieldEntity.setAnnotationType(!"GroupBy".equals(annotationType) && !"OrderBy".equals(annotationType)?
                        annotationType:fieldEntity.getAnnotationType());
    }

    /**
     * 区间查询注解需要指定数据库字段映射
     * @param column  数据库映射字段
     * @param annotationType  注解类型
     */
    private static void checkColumn(String column,String annotationType){
        if (StringUtils.isBlank(column)){
            throw new IllegalArgumentException(String.format("@%s 注解必须指定 column 的值！",annotationType));
        }
    }

    /**
     *  获取泛型查询参数
     * @param ms
     * @return
     */
    public static Class<?> getParamClass(MappedStatement ms,Class<?> mapper) {
        String msId = ms.getId();
        if (paramClassMap.containsKey(msId)) {
            return (Class<?>)paramClassMap.get(msId);
        } else {
            Class<?> mapperClass = getMapperClass(msId);
            Type[] types = mapperClass.getGenericInterfaces();
            Type[] var5 = types;
            int var6 = types.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Type type = var5[var7];
                if (type instanceof ParameterizedType) {
                    ParameterizedType t = (ParameterizedType)type;
                    if (t.getRawType() == mapper || mapper.isAssignableFrom((Class)t.getRawType())) {
                        Class<?> returnType = (Class)t.getActualTypeArguments()[1];
                        paramClassMap.put(msId,returnType);
                        return returnType;
                    }
                }
            }
            throw new MapperException(String.format("获取参数对象属性失败 %s",msId));
        }
    }
}
