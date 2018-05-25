package hx.data.mybatis.reflect.filter;

/**
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:23:24
 *
 */
abstract class PrivateStrings {
	/**
	 * 判断一个字符串valueItem是否类似于字符串数组array中的一个元素,其中用到了比较方法:直接相等(equals),以开头(
	 * startsWith),以结尾(endsWith);
	 */
	public static final boolean likesIn(final String valueItem, final String[] array) {
		if (null == array || null == valueItem || "".equals(valueItem.trim()))
			return false;

		for (final String value : array) {
			if (valueItem.equals(value) || valueItem.startsWith(value) || valueItem.endsWith(value))
				return true;
		}
		return false;
	}

}
