package hx.data.mybatis.cglib;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author mingliang
 * @Date 2017-09-26 9:59
 */
public class ObjectPerson implements Serializable{
    private String name;
    private String address;
    private Integer size;
    private Date createTime;
    private Date updateTime;
    private Integer version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "name:"+this.name+",address:"+this.address+",size:"+this.size+",createTime:"+this.createTime+",updateTime"+
                this.updateTime+",version:"+this.version;
    }
}
