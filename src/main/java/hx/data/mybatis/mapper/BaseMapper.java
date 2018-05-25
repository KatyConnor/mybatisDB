package hx.data.mybatis.mapper;

import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @Author mingliang
 * @Date 2017-09-13 14:44
 */
@RegisterMapper
public interface BaseMapper<T,E> extends Mapper<T>, MySqlMapper<T>,
        BaseSelectConditionMapper<T,E>,
        BaseUpdateListDynamicConditionMapper<T,E>,
        BaseDeleteDynamicConditionMapper<E>,
        BaseInsertListMapper<T>,
        ConditionMapper<T>{

}
