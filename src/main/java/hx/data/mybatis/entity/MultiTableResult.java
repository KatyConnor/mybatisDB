package hx.data.mybatis.entity;

import hx.data.mybatis.commonenum.ContinuousTableTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 连表查询结果处理
 * @Author mingliang
 * @Date 2018-03-06 18:06
 */
public class MultiTableResult<T> {

    // 表映射对象
    protected List<Object> tables = new ArrayList<>();
    // 连表查询方式
    private ContinuousTableTypeEnum likeType;

    public List<Object> resultList;

    // 将所有的的对象转换成一个对象
    public List<T> getResultObject(){
        return null;
    }

}
