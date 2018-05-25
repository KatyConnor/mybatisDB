package hx.data.mybatis.reflect.filter;

import java.net.URL;

/**
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:23:05
 *
 */
public interface ISubTypeFilter {
	public boolean accept(Class<?> baseType, URL pathUrl, String typePath);
}
