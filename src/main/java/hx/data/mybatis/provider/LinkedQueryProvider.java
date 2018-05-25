package hx.data.mybatis.provider;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author mingliang
 * @Date 2018-03-27 15:30
 */
public class LinkedQueryProvider extends MapperTemplate {

    private Class<?> mapperClass;

    public LinkedQueryProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
        this.mapperClass = mapperClass;
    }

    public SqlNode setSqlNode(MappedStatement ms, String methodName){
        System.out.println("执行链表查询！method = "+ methodName);
        List<SqlNode> sqlNodes = new ArrayList<>();
        return new MixedSqlNode(sqlNodes);
    }

    public SqlNode insertObjectListsss(MappedStatement ms){
        System.out.println("执行链表查询！");
        List<SqlNode> sqlNodes = new ArrayList<>();
        return new MixedSqlNode(sqlNodes);
    }
}
