package hx.data.mybatis.mapper;

import hx.data.mybatis.provider.BaseLinkMapperSelectProvider;
import hx.data.mybatis.provider.BaseMapperSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * 多表查询
 * @Author mingliang
 * @Date 2017-11-24 11:24
 */
@RegisterMapper
public interface BaseLinkSelectMapper<T,E> {

    @SelectProvider(
            type = BaseLinkMapperSelectProvider.class,
            method = "dynamicSQL"
    )
    List<T> findMultiTable(E e);
}
