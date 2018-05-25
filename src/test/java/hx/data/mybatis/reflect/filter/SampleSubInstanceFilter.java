package hx.data.mybatis.reflect.filter;

import java.lang.reflect.Modifier;

/**
 * 过滤所有的非抽象和非接口的子类
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:23:30
 *
 */
public class SampleSubInstanceFilter extends SampleSubTypeFilter {
	public static final ISubTypeFilter DEFAULT = new SampleSubInstanceFilter();

	public SampleSubInstanceFilter() {
	}

	@Override
	protected boolean filterForInstance(final Class<?> clazz) {
		final int modifier = clazz.getModifiers();
		return (!Modifier.isAbstract(modifier) && !Modifier.isInterface(modifier));
	}
}
