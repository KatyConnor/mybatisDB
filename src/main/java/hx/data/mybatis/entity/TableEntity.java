package hx.data.mybatis.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据库表映射实体类
 * @Author mingliang
 * @Date 2017-11-23 11:36
 */
public class TableEntity implements Serializable {

    private String tableName;
    private List<TableViewRelationEntity> tableViewRelationEntities;
    private Map<String,ColumnEntity> columnEntity;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableViewRelationEntity> getTableViewRelationEntities() {
        return tableViewRelationEntities;
    }

    public void setTableViewRelationEntities(List<TableViewRelationEntity> tableViewRelationEntities) {
        this.tableViewRelationEntities = tableViewRelationEntities;
    }

    public Map<String, ColumnEntity> getColumnEntity() {
        return columnEntity;
    }

    public void setColumnEntity(Map<String, ColumnEntity> columnEntity) {
        this.columnEntity = columnEntity;
    }
}
