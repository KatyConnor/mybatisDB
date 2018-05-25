package hx.data.mybatis.reflect.filter;

/**
 * 过滤文件的是否符合要求
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:23:40
 *
 */
public class TypeFilter implements ITypeFilter {
	public static final ITypeFilter DEFAULT = new TypeFilter(".class");
	private String[] exts;

	public TypeFilter(final String... exts) {
		this.exts = exts;
	}

	@Override
	public boolean accept(final String typeName) {
		if (null == exts)
			return true;
		return PrivateStrings.likesIn(typeName, exts);
	}
}
