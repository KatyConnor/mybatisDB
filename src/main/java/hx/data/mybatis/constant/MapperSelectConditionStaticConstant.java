package hx.data.mybatis.constant;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author mingliang
 * @Date 2017-09-14 11:26
 */
public final class MapperSelectConditionStaticConstant {
    public final static  String LIKE = "Like";
    public final static  String EQUAL_TO = "EqualTo";
    public final static  String BETWEEN_START = "BetweenStart";
    public final static  String BETWEEN_END = "BetweenEnd";
    public final static  String GREATER_THAN = "GreaterThan";
    public final static  String GREATER_THAN_OR_EQUAL_TO = "GreaterThanOrEqualTo";
    public final static  String IN = "In";
    public final static  String GROUP_BY = "GroupBy";
    public final static  String IS_NOT_NULL = "IsNotNull";
    public final static  String IS_NULL = "IsNull";
    public final static  String LESS_THAN = "LessThan";
    public final static  String LESS_THAN_OR_EQUAL_TO = "LessThanOrEqualTo";
    public final static  String NOT_BETWEEN_START = "NotBetweenStart";
    public final static  String NOT_BETWEEN_END = "NotBetweenEnd";
    public final static  String NOT_EQUAL_TO = "NotEqualTo";
    public final static  String NOT_IN = "NotIn";
    public final static  String NOT_LIKE = "NotLike";
    public final static  String ORDER_BY = "OrderBy";
    private static String[] value = new String[]{"Like","EqualTo","BetweenStart","BetweenEnd","GreaterThan","GreaterThanOrEqualTo",
            "In","GroupBy","IsNotNull","IsNull","LessThan","LessThanEqualTo","NotBetweenStart","NotBetweenEnd",
            "NotEqualTo","NotIn","NotLike","OrderBy"};
    private static List<String> keyList = new ArrayList<>();
    static {
        keyList.addAll(CollectionUtils.arrayToList(value));
    }

    public static boolean check(String code){
        return keyList.contains(code);
    }
}
