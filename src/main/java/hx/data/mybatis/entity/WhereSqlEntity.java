package hx.data.mybatis.entity;

import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TrimSqlNode;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * sql查询条件
 * @Author mingliang
 * @Date 2017-09-25 17:59
 */
public class WhereSqlEntity {

    // if sql条件
    private List<SqlNode> ifNodes;

    private TrimSqlNode trimSqlNode;

    // 排序默认倒叙排列
    private String orderBy;

    // 分组
    private String groupBy;

    public List<SqlNode> getIfNodes() {
        return ifNodes;
    }

    public void setIfNodes(List<SqlNode> ifNodes) {
        if (CollectionUtils.isEmpty(this.ifNodes)){
            this.ifNodes = ifNodes;
            return;
        }
        this.ifNodes.addAll(ifNodes);
    }

    public TrimSqlNode getTrimSqlNode() {
        return trimSqlNode;
    }

    public void setTrimSqlNode(TrimSqlNode trimSqlNode) {
        this.trimSqlNode = trimSqlNode;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

}
