package hx.data.mybatis.mapper;

import hx.data.mybatis.provider.BaseMapperInsertListProvider;
import org.apache.ibatis.annotations.InsertProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @Author mingliang
 * @Date 2017-09-22 10:50
 */
@RegisterMapper
public interface BaseInsertListMapper<T> {

    @InsertProvider(
            type = BaseMapperInsertListProvider.class,
            method = "dynamicSQL"
    )
    int insertLists(List<T> list);
}
