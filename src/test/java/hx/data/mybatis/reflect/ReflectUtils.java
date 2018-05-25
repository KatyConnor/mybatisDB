package hx.data.mybatis.reflect;

import hx.data.mybatis.reflect.filter.PathURLFilter;
import hx.data.mybatis.reflect.filter.SampleSubInstanceFilter;
import hx.data.mybatis.reflect.filter.TypeFilter;

import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:24:02
 *
 */
public final class ReflectUtils {
	public static final String VAR_START_FLAG = "${";
	public static final String VAR_END_FLAG = "}";

	private static Reflections sharedReflections;
	static final Collection<String> EMP_COLL = Collections.emptyList();

	public static final void createSharedReflections(final String... filterExts) {
		final Reflections refs = new Reflections();
		refs.addPathURLFilter(new PathURLFilter(filterExts));//
		refs.addTypeFilter(TypeFilter.DEFAULT);
		refs.setSubTypeFilter(SampleSubInstanceFilter.DEFAULT);
		ReflectUtils.setSharedReflections(refs);
	}

	/**
	 * 此方法用于绑定一个通用的共享类型遍列工具.
	 * @param sharedReflections
	 */
	public static final void setSharedReflections(final Reflections sharedReflections) {
		ReflectUtils.sharedReflections = sharedReflections;
	}

	/**
	 * 调用此方法之前必须先设置共享的类型遍列工具,参考:{@link #setSharedReflections(Reflections)},
	 * 此方法主要使更方便的遍列给定类的实现,
	 */
	public static final Collection<String> listSubClass(final Class<?> baseType, final String... typeNames) {//
		if (null == sharedReflections)
			return EMP_COLL;
		//调用阶段由于可能增加新的子类实现,需要每次都重新扫描,只有在发布的产品时使用保存记录的方法以提高启动速度.
		return Beans.isDesignTime() ? sharedReflections.getSubTypes(baseType, typeNames) : sharedReflections.getSubTypesFast(baseType);
	}

	public static List<Class<?>> listClassOfPackage(final Class<?> cType, final String extenion) {
		final List<Class<?>> result = new ArrayList<Class<?>>();
		final List<String> cPath = ReflectUtils.listClassCanonicalNameOfPackage(cType, extenion);
		for (final String path : cPath) {
			try {
				result.add(Class.forName(path, false, Thread.currentThread().getContextClassLoader()));
			} catch (final Exception e) {
				// ignore
			}
		}
		return result;
	}

	public static List<String> listClassCanonicalNameOfPackage(final Class<?> clazz, final String extenion) {
		return ReflectUtils.listNameOfPackage(clazz, extenion, true);
	}

	public static List<String> listClassNameOfPackage(final Class<?> clazz, final String extenion) {
		return ReflectUtils.listNameOfPackage(clazz, extenion, false);
	}

	public static List<String> listNameOfPackage(final Class<?> clazz, final String extenion, final boolean fullPkgName) {
		return ReflectUtils.listNameOfPackage(clazz.getName().replace('.', '/') + ".class", extenion, fullPkgName);
	}

	public static List<String> listNameOfPackage(final String clazzPkg, final String extenion, final boolean fullPkgName) {
		final List<String> result = new ArrayList<String>();

		final StringBuffer pkgBuf = new StringBuffer(clazzPkg);

		if (pkgBuf.charAt(0) != '/')
			pkgBuf.insert(0, '/');

		final URL urlPath = ReflectUtils.class.getResource(pkgBuf.toString());

		if (null == urlPath)
			return result;

		String checkedExtenion = extenion;
		if (!extenion.endsWith(".class"))
			checkedExtenion = extenion + ".class";

		if (pkgBuf.toString().endsWith(".class"))
			pkgBuf.delete(pkgBuf.lastIndexOf("/"), pkgBuf.length());

		pkgBuf.deleteCharAt(0);

		final StringBuffer fileUrl = new StringBuffer();
		try {
			fileUrl.append(URLDecoder.decode(urlPath.toExternalForm(), "UTF-8"));
		} catch (final UnsupportedEncodingException e1) {
			fileUrl.append(urlPath.toExternalForm());
		}

		if (fileUrl.toString().startsWith("file:")) {
			fileUrl.delete(0, 5);// delete file: flag
			if (fileUrl.indexOf(":") != -1)
				fileUrl.deleteCharAt(0);// delete flag
			final String baseDir = fileUrl.substring(0, fileUrl.lastIndexOf("classes") + 8);
			ReflectUtils.doListNameOfPackageInDirectory(new File(baseDir), new File(baseDir), result, pkgBuf.toString(), checkedExtenion, fullPkgName);
		} else {
			ReflectUtils.doListNameOfPackageInJar(urlPath, urlPath, result, pkgBuf.toString(), checkedExtenion, fullPkgName);
		}

		return result;
	}

	/**
	 */
	private static void doListNameOfPackageInJar(final URL baseUrl, final URL urlPath, final List<String> result, final String clazzPkg, final String extenion, final boolean fullPkgName) {
		try {
			// It does not work with the filesystem: we must
			// be in the case of a package contained in a jar file.
			final JarURLConnection conn = (JarURLConnection) urlPath.openConnection();
			final JarFile jfile = conn.getJarFile();
			final Enumeration<JarEntry> e = jfile.entries();

			ZipEntry entry;
			String entryname;

			while (e.hasMoreElements()) {
				entry = e.nextElement();
				entryname = entry.getName();

				if (entryname.startsWith(clazzPkg) && entryname.endsWith(extenion)) {
					if (fullPkgName)
						result.add(entryname.substring(0, entryname.lastIndexOf('.')).replace('/', '.'));
					else
						result.add(entryname.substring(entryname.lastIndexOf('/') + 1, entryname.lastIndexOf('.')));
				}
			}
		} catch (final IOException ioex) {
		}
	}

	private static void doListNameOfPackageInDirectory(final File baseDirectory, final File directory, final List<String> result, final String clazzPkg, final String extenion,
													   final boolean fullPkgName) {
		File[] files = directory.listFiles();
		if (null == files)
			files = new File[] {};
		String clazzPath;
		final int baseDirLen = baseDirectory.getAbsolutePath().length() + 1;
		for (final File file : files) {
			if (file.isDirectory()) {
				ReflectUtils.doListNameOfPackageInDirectory(baseDirectory, file, result, clazzPkg, extenion, fullPkgName);
			} else {
				if (!file.getName().endsWith(extenion))
					continue;

				if (fullPkgName) {
					clazzPath = file.getAbsolutePath().substring(baseDirLen);
					clazzPath = clazzPath.substring(0, clazzPath.length() - 6);
					result.add(clazzPath.replace(File.separatorChar, '.'));
				} else {
					result.add(file.getName().substring(0, file.getName().length() - 6));
				}
			}
		}
	}

	public static final <T> T initClass(final String implClass, final Class<T> tType) {
		return ReflectUtils.initClass(implClass, tType, true);
	}

	public static final <T> T initClass(final String implClass, final Class<T> tType, final boolean doInit) {
		try {
			final Object object = Class.forName(implClass, doInit, Thread.currentThread().getContextClassLoader()).newInstance();
			return tType.cast(object);
		} catch (final Throwable e) {
			return null;
		}
	}
}
