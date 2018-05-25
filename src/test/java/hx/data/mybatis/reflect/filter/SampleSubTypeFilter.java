package hx.data.mybatis.reflect.filter;

import java.net.URL;

/**
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:23:35
 *
 */
public class SampleSubTypeFilter implements ISubTypeFilter {
	public static final ISubTypeFilter DEFAULT = new SampleSubTypeFilter();

	public SampleSubTypeFilter() {
	}

	@Override
	public boolean accept(final Class<?> baseType, final URL pathUrl, final String typePath) {
		String typeDef = typePath.replace('/', '.');
		typeDef = typeDef.substring(0, typeDef.lastIndexOf('.'));
		if (typeDef.equals(baseType.getName()))
			return false;
		Class<?> clazz;
		try {
			clazz = Class.forName(typeDef, false, Thread.currentThread().getContextClassLoader());
		} catch (final Throwable e) {
			return false;
		}
		if (!filterForInstance(clazz))
			return false;

		if (baseType.isAssignableFrom(clazz))
			return true;
		return false;
	}

	/**
	 * @param clazz
	 * @return
	 */
	protected boolean filterForInstance(final Class<?> clazz) {
		return true;
	}
}
