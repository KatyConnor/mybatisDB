package hx.data.mybatis.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 表关联关系实体
 *
 * @Author mingliang
 * @Date 2017-11-23 11:57
 */
public class TableViewRelationEntity implements Serializable{

    /**关联表明 */
    private String tableName;
    /** 关联字段 */
    private List<String> foreignKeys;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
}
