package hx.data.mybatis.mapper;

import hx.data.mybatis.provider.BaseMapperDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * @Author mingliang
 * @Date 2017-09-22 10:51
 */
@RegisterMapper
public interface BaseDeleteDynamicConditionMapper<E> {

    @DeleteProvider(
            type = BaseMapperDeleteProvider.class,
            method = "dynamicSQL"
    )
    int deleteDynamicConditional(E e);
}
