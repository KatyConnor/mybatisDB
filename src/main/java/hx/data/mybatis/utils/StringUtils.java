package hx.data.mybatis.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author mingliang
 * @Date 2017-11-24 14:35
 */
public class StringUtils {

    private static final String STR_UNDERLINE = "_";
    private static final char CHAR_UNDERLINE = '_';
    private static final String EMPTITY = "";
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 下划线转驼峰格式,采用循环字符长度去字节判断转换，
     *  此种转换方式相对效率更高
     * @param param
     * @return
     */
    public static String convertUnderlineToHump(String param){
        if (param == null || EMPTITY.equals(param.trim())) {
            return EMPTITY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == CHAR_UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转换为驼峰，效率convertUnderlineToHump 差不多
     *
     * @param underscoreName
     * @return
     */
    public static String convertUnderlineToHump1(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if (STR_UNDERLINE.charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * 下划线转驼峰格式,采用正则验证是否包含'_'，然后找到下滑线的位置，替换成 '_' 后面字母的大写，
     * 此种方式相对耗时，效率较低
     * @param param
     * @return
     */
    @Deprecated
    public static String convertUnderlineToHump2(String param){
        if (param == null || EMPTITY.equals(param.trim())) {
            return EMPTITY;
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile(STR_UNDERLINE).matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰，效率低于 convertUnderlineToHump
     * @param str
     * @return
     */
    public static String convertUnderlineToHump3(String str){
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰格式字符串转换为下划线格式字符串，采用循环字符长度取字节进行判断转换
     *  效率相对更高，耗时更少
     * @param param
     * @return 小写字符串
     */
    public static String convertHumpToUnderline(String param) {
        if (param == null || EMPTITY.equals(param.trim())) {
            return EMPTITY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(STR_UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线，且转为大写
     * @param param
     * @return 大写字符串
     */
    public static String convertHumpToUnderlineAndUpperCase(String param) {
        return convertHumpToUnderline(param).toUpperCase();
    }

    /**
     * 驼峰转换为下划线,效率convertHumpToUnderline差不多
     *
     * @param camelCaseName
     * @return
     */
    public static String convertHumpToUnderline1(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            result.append(camelCaseName.substring(0, 1).toLowerCase());
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append(STR_UNDERLINE);
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    /**
     * 驼峰转下划线 简单写法，效率低
     * @param str
     * @return
     */
    public static String convertHumpToUnderline2(String str){
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    /**
     * 驼峰转下划线,效率不是很稳定，比convertHumpToUnderline2效率稍高
     * @param str
     * @return
     */
    public static String convertHumpToUnderline3(String str){
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, STR_UNDERLINE+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

    public static boolean isBlank(String str){
        return str == null || EMPTITY.equals(str.trim());
    }

}
