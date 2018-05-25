package hx.data.mybatis.commonenum;

/**
 * 表关联关系
 * @Author mingliang
 * @Date 2017-11-23 17:56
 */
public enum ContinuousTableTypeEnum {

    /** 内连接 */
    INNER_JION("INNER_JION"," INNER JION "),
    /** 自连接 */
    JION("JOIN"," JOIN "),
    /** 左外连接 */
    LEFT_OUTER_JOIN("LEFT_OUTER_JOIN"," LEFT OUTER JOIN "),
    /** 右外连接 */
    RIGHT_OUTER_JOIN("RIGHT_OUTER_JOIN"," RIGHT OUTER JOIN "),
    /** 全外连接 */
    FULL_OUTER_JOIN("FULL_OUTER_JOIN"," FULL OUTER JOIN "),
    /** 传统连接方式 */
    WHERE("WHERE"," WHERE "),
    /** 子查询 */
    CHILD_WHERE("CHILD_WHERE"," WHERE ");

    private String code;
    private String message;

    ContinuousTableTypeEnum(String code, String message) {
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
}
