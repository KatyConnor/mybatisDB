package hx.data.mybatis.mapper;

import hx.data.mybatis.provider.BaseMapperUpdateListProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @Author mingliang
 * @Date 2017-09-22 10:49
 */
@RegisterMapper
public interface BaseUpdateListDynamicConditionMapper<T,E> {

    @UpdateProvider(
            type = BaseMapperUpdateListProvider.class,
            method = "dynamicSQL"
    )
    int updateListDynamicConditional(List<T> list,E e);
}
