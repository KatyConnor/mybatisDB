package hx.data.mybatis.aop;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * @Author mingliang
 * @Date 2017-11-30 18:11
 */
public class ComparatorDate implements Comparator {

    public final static String ASC = "ASC";
    public final static String DESC = "DESC";

    private static SimpleDateFormat format;
    private static String compareField;
    private static String sortOrder;

    @Override
    public int compare(Object o1, Object o2) {
        if (StringUtils.isBlank(compareField)){
            throw new IllegalArgumentException("请指明排序的字段！");
        }
        if (StringUtils.isBlank(sortOrder)){
            sortOrder = DESC;
        }
        try {
            Field field1 = o1.getClass().getDeclaredField(compareField);
            Field field2 = o2.getClass().getDeclaredField(compareField);
            field1.setAccessible(true);
            field2.setAccessible(true);
            String type1 = field1.getType().getSimpleName();
            String type2 = field2.getType().getSimpleName();
            if (type1.equals("Date") && type2.equals("Date")){
                Date date1 = (Date) field1.get(o1);
                Date date2 = (Date) field2.get(o2);
                return sortOrder.equals(ASC)?date1.compareTo(date2):date2.compareTo(date1);
            } else if (type1.equals("String") && type2.equals("String")){
                String date1 = (String) field1.get(o1);
                String date2 = (String) field2.get(o2);
                return sortOrder.equals(ASC)?date1.compareTo(date2):date2.compareTo(date1);
            } else if (type1.equals("Timestamp") && type2.equals("Timestamp")){
                Timestamp date1 = (Timestamp) field1.get(o1);
                Timestamp date2 = (Timestamp) field2.get(o2);
                return sortOrder.equals(ASC)?date1.compareTo(date2):date2.compareTo(date1);
            }
        } catch (NoSuchFieldException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        }
        return -1;
    }

    /**
     * 时间排序，设置排序时间的格式，
     * @param dateFormat
     */
    public void setFormat(String dateFormat){
        format = new SimpleDateFormat(dateFormat);
    }

    /**
     * 排序的字段
     * @param fieldName
     */
    public void setCompareField(String fieldName){
        compareField = fieldName;
    }

    /**
     * 排序方式 DESC 降序 ASC 升序
     * @param order
     */
    public void setSortOrder(String order){
        sortOrder = order;
    }
}
