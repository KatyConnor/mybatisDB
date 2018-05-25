package hx.data.mybatis.aop;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author mingliang
 * @Date 2017-11-29 17:31
 */
public class User {

    private String name;
    private Integer age;
    private String telNo;
    private String date;
    private Date createTime;
    private Timestamp createTime1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Timestamp getCreateTime1() {
        return createTime1;
    }

    public void setCreateTime1(Timestamp createTime1) {
        this.createTime1 = createTime1;
    }
}
