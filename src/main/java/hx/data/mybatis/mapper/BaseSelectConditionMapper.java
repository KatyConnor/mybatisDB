package hx.data.mybatis.mapper;

import hx.data.mybatis.provider.BaseMapperSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @Author mingliang
 * @Date 2017-09-21 21:48
 */
@RegisterMapper
public interface BaseSelectConditionMapper<T,E> {

    @SelectProvider(
            type = BaseMapperSelectProvider.class,
            method = "dynamicSQL"
    )
    List<T> findDynamicConditional(E e);


    @SelectProvider(
            type = BaseMapperSelectProvider.class,
            method = "dynamicSQL"
    )
    List<T> findDynamicConditionalByPage(E e);
}
