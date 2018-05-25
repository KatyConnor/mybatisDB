package hx.data.mybatis.reflect.filter;

import java.io.File;
import java.net.URL;

/**
 *
 * 过滤提供的路径是否符合规则
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:23:14
 *
 */
public class PathURLFilter implements IPathURLFilter {
	public static final IPathURLFilter DEFAULT = new PathURLFilter("/", ".jar");
	private String[] exts;

	public PathURLFilter(final String... exts) {
		this.exts = exts;
	}

	@Override
	public boolean accept(final URL pathURL) {
		// for any
		if (null == exts)
			return true;
		// do filter
		return PrivateStrings.likesIn(new File(pathURL.getFile()).getName(), exts);
	}
}
