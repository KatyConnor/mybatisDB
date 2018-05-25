package hx.data.mybatis.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @Author mingliang
 * @Date 2017-11-20 13:50
 */
public class FieldEntity {

    private String fieldName;
    private Field field;
    private String column;
    private Annotation annotation;
    private String annotationType;
    private String fieldNameStart;
    private String fieldNameEnd;
    private String start;
    private String end;
    private String type;
    private boolean groupBy;
    private boolean orderBy;
    private String orderType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public String getFieldNameStart() {
        return fieldNameStart;
    }

    public void setFieldNameStart(String fieldNameStart) {
        this.fieldNameStart = fieldNameStart;
    }

    public String getFieldNameEnd() {
        return fieldNameEnd;
    }

    public void setFieldNameEnd(String fieldNameEnd) {
        this.fieldNameEnd = fieldNameEnd;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isGroupBy() {
        return groupBy;
    }

    public void setGroupBy(boolean groupBy) {
        this.groupBy = groupBy;
    }

    public boolean isOrderBy() {
        return orderBy;
    }

    public void setOrderBy(boolean orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
