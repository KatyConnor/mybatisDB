package hx.data.mybatis.commonenum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author mingliang
 * @Date 2017-11-23 16:41
 */
public enum MapperSelectConditionStaticConstantEnum {
    LIKE("Like"," LIKE "),
    EQUAL_TO("EqualTo"," = "),
    BETWEEN_START("BetweenStart"," BETWEEN "),
    BETWEEN_END("BetweenEnd"," AND "),
    GREATER_THAN("GreaterThan"," > "),
    GREATER_THAN_OR_EQUAL_TO("GreaterThanOrEqualTo"," >= "),
    IN("In"," IN "),
    IS_NOT_NULL("IsNotNull"," IS NOT NULL "),
    IS_NULL("IsNull"," IS NULL "),
    LESS_THAN("LessThan"," < "),
    LESS_THAN_OR_EQUAL_TO("LessThanOrEqualTo"," <= "),
    NOT_BETWEEN_START("NotBetweenStart"," NOT BETWEEN "),
    NOT_BETWEEN_END("NotBetweenEnd"," AND "),
    NOT_EQUAL_TO("NotEqualTo"," <> "),
    NOT_IN("NotIn"," NOT IN "),
    NOT_LIKE("NotLike"," NOT LIKE "),
    LINK_FIELD("LinkField","LinkField"),
    ORDER_BY("OrderBy"," ORDER BY "),
    GROUP_BY("GroupBy"," GROUP BY ");

    private String code;
    private String message;

    MapperSelectConditionStaticConstantEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *  根据code获取枚举描述
     * @param code
     * @return
     */
    public static String getMsgByCode(String code){
        List<MapperSelectConditionStaticConstantEnum> _enumList = getAll();
        for (MapperSelectConditionStaticConstantEnum _enum : _enumList){
            if (_enum.getCode().equals(code)){
                return _enum.getMessage();
            }
        }
        return null;
    }

    /**
     * 根据code获取枚举类
     * @param code
     * @return
     */
    public static MapperSelectConditionStaticConstantEnum getByCode(String code){
        List<MapperSelectConditionStaticConstantEnum> errorcedeEnumList = getAll();
        for (MapperSelectConditionStaticConstantEnum _enum : errorcedeEnumList){
            if (_enum.getCode().equals(code)){
                return _enum;
            }
        }
        return null;
    }

    /**
     * 判断枚举code是否在枚举中,GROUP_BY, ORDER_BY除外
     * @param code
     * @return
     */
    public static boolean check(String code){
        List<MapperSelectConditionStaticConstantEnum> enumList = new ArrayList<>(getAll());
        enumList.remove(GROUP_BY);
        enumList.remove(ORDER_BY);
        enumList.remove(LINK_FIELD);
        return enumList.contains(MapperSelectConditionStaticConstantEnum.getByCode(code));
    }

    /**
     * 获取所有枚举
     * @return
     */
    public static List<MapperSelectConditionStaticConstantEnum> getAll(){
        return Arrays.asList(MapperSelectConditionStaticConstantEnum.values());
    }

}
