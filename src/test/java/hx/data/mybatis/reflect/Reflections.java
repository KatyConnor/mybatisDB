package hx.data.mybatis.reflect;

import hx.data.mybatis.reflect.filter.IPathURLFilter;
import hx.data.mybatis.reflect.filter.ISubTypeFilter;
import hx.data.mybatis.reflect.filter.ITypeFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


/**
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午3:23:49
 *
 */
public final class Reflections {
	private final Collection<URL> pathUrls;
	private final Collection<IPathURLFilter> pathURLfilters;
	private final Collection<ITypeFilter> typeFilters;
	private ISubTypeFilter subTypeFilter;

	public Reflections() {
		typeFilters = new ArrayList<ITypeFilter>();
		pathURLfilters = new ArrayList<IPathURLFilter>();
		this.pathUrls = ClasspathHelper.getUrlsForCurrentClasspath();
	}

	public Reflections(final Collection<URL> pathUrls) {
		this.pathUrls = pathUrls;
		typeFilters = new ArrayList<ITypeFilter>();
		pathURLfilters = new ArrayList<IPathURLFilter>();
	}

	/**
	 * @param subTypeFilter
	 *            the subTypeFilter to set
	 */
	public void setSubTypeFilter(final ISubTypeFilter subTypeFilter) {
		this.subTypeFilter = subTypeFilter;
	}

	/**
	 * @return the subTypeFilter
	 */
	public ISubTypeFilter getSubTypeFilter() {
		return subTypeFilter;
	}

	public Reflections addPathURLFilter(final IPathURLFilter pathURLFilter) {
		if (null == pathURLFilter)
			return this;
		if (!this.pathURLfilters.contains(pathURLFilter))
			this.pathURLfilters.add(pathURLFilter);
		return this;
	}

	public Reflections addTypeFilter(final ITypeFilter typeFilter) {
		if (null == typeFilter)
			return this;
		if (!this.typeFilters.contains(typeFilter))
			this.typeFilters.add(typeFilter);
		return this;
	}

	private static final String histFile = "./configuration.REF";
	private Document histDom;

	public Collection<String> getSubTypesFast(final Class<?> baseType) {//, final String... typeNames
		//首先过滤出当前允许扫描的路径
		final StringBuilder bufPathsId = new StringBuilder(32);
		final Map<File, URL> fileUrls = new LinkedHashMap<File, URL>(8);
		for (final URL pathUrl : pathUrls) {
			if (!acceptPathUrl(pathUrl))
				continue;
			File file = null;
			try {
				file = new File(URLDecoder.decode(pathUrl.getFile(), "UTF-8"));
			} catch (final Exception e) {
				file = new File(pathUrl.getFile());
			}
			fileUrls.put(file, pathUrl);
			if (!file.exists())//is url file?ignore
				continue;
			bufPathsId.append(file.getName()).append(file.lastModified());
		}
		final String domId = MD5.getHashString(bufPathsId.toString());
		if (null == histDom)
			histDom = W3cUtils.readXml(histFile);
		if (null == histDom)
			histDom = W3cUtils.newDom("R");
		Element rootEle = histDom.getDocumentElement();
		if (null == rootEle)
			histDom.appendChild(rootEle = histDom.createElement("R"));
		if (!domId.equals(rootEle.getAttribute("id"))) {
			rootEle.getParentNode().removeChild(rootEle);
			histDom.appendChild(rootEle = histDom.createElement("R"));
			rootEle.setAttribute("id", domId);
		}
		final String baseTypeId = MD5.getHashString(baseType.getName());
		Element refEle = W3cUtils.firstChildElement(rootEle, "E", "id", baseTypeId);
		if (null != refEle) {
			final List<Element> valueEles = W3cUtils.childElementList(refEle, "F");
			final Collection<String> result = new ArrayList<String>(valueEles.size());
			for (final Element valueEle : valueEles) {
				result.add(new String(Base64.decodeFast(valueEle.getAttribute("id"))));
			}
			return result;
		}
		final ThreadPool<ListSubTypes> pool = new ThreadPool<ListSubTypes>();
		for (final File fileKey : fileUrls.keySet()) {
			pool.execute(new ListSubTypes(baseType, fileKey, fileUrls.get(fileKey)));
		}
		try {
			pool.shutdown(3, TimeUnit.MINUTES);
		} catch (final InterruptedException e) {
			e.printStackTrace();//for debug
		}
		final Collection<String> result = new ArrayList<String>();
		for (final ListSubTypes task : pool.getThreadRunables()) {
			result.addAll(task.result);
		}
		refEle = W3cUtils.addEle(rootEle, "E");
		refEle.setAttribute("id", baseTypeId);
		for (final String itm : result) {
			W3cUtils.addEle(refEle, "F").setAttribute("id", Base64.encodeToString(itm.getBytes(), false));
		}
		try {
			W3cUtils.writeXmlDocument(histFile, histDom);
		} catch (final Exception e) {
		}
		return result;
	}

	/**
	 * @see {@link ReflectUtils#createSharedReflections(String...)}
	 * @see {@link ReflectUtils#setSharedReflections(Reflections)}
	 * @see {@link ReflectUtils#listSubClass(Class)}
	 * @param baseType
	 * @return
	 */
	public Collection<String> getSubTypes(final Class<?> baseType, final String... typeNames) {//
		final ThreadPool<ListSubTypes> pool = new ThreadPool<ListSubTypes>();
		for (final URL pathUrl : pathUrls) {
			if (!acceptPathUrl(pathUrl))
				continue;
			File file = null;
			try {
				file = new File(URLDecoder.decode(pathUrl.getFile(), "UTF-8"));
			} catch (final Exception e) {
				file = new File(pathUrl.getFile());
			}
			pool.execute(new ListSubTypes(baseType, file, pathUrl, typeNames));
		}
		try {
			pool.shutdown(3, TimeUnit.MINUTES);
		} catch (final InterruptedException e) {
			e.printStackTrace();//for debug
		}
		final Collection<String> result = new ArrayList<String>();
		for (final ListSubTypes task : pool.getThreadRunables()) {
			result.addAll(task.result);
		}
		return result;
	}

	class ListSubTypes implements Runnable {
		final File file;
		final Class<?> baseType;
		final URL pathUrl;
		final String[] typeNames;

		public ListSubTypes(final Class<?> baseType, final File file, final URL pathUrl, final String... typeNames) {
			this.baseType = baseType;
			this.file = file;
			this.pathUrl = pathUrl;
			this.typeNames = typeNames;
		}

		Collection<String> result = new ArrayList<String>(4);

		@Override
		public void run() {
			if (file.isDirectory()) {
				listSubTypesFromDirectory(file, baseType, pathUrl, file, result, typeNames);
			} else
				listSubTypesFromJar(baseType, pathUrl, result, typeNames);
		}
	}

	/**
	 * @param baseType
	 * @param pathUrl
	 * @param result
	 */
	public void listSubTypesFromDirectory(final File baseDirectory, final Class<?> baseType, final URL pathUrl, final File directory,
										  final Collection<String> result, final String... typeNames) {
		File[] files = directory.listFiles();
		if (null == files)
			files = new File[] {};
		String clazzPath;
		final int baseDirLen = baseDirectory.getAbsolutePath().length() + 1;
		for (final File file : files) {
			if (file.isDirectory()) {
				listSubTypesFromDirectory(baseDirectory, baseType, pathUrl, file, result, typeNames);
			} else {
				clazzPath = file.getAbsolutePath().substring(baseDirLen);
				clazzPath = clazzPath.replace('\\', '/');
				doTypesFilter(baseType, pathUrl, result, clazzPath, typeNames);
			}
		}
	}

	/**
	 * @param baseType
	 * @param pathUrl
	 * @param result
	 */
	public void listSubTypesFromJar(final Class<?> baseType, URL pathUrl, final Collection<String> result, final String... typeNames) {
		try {
			// It does not work with the filesystem: we must
			// be in the case of a package contained in a jar file.
			JarFile jarFile = null;
			try {
				if ("file".equals(pathUrl.getProtocol()))
					pathUrl = new URL("jar:" + pathUrl.toExternalForm() + "!/");
				jarFile = ((JarURLConnection) pathUrl.openConnection()).getJarFile();
			} catch (final Exception e) {
				final String filePath = pathUrl.getFile();
				// if on win platform
				if (filePath.indexOf(':') != -1) {
					if (pathUrl.getFile().charAt(0) == '/')
						jarFile = new JarFile(filePath.substring(1));
				}
				if (null == jarFile)
					jarFile = new JarFile(filePath);
			}
			final Enumeration<JarEntry> e = jarFile.entries();
			ZipEntry entry;
			while (e.hasMoreElements()) {
				entry = e.nextElement();
				doTypesFilter(baseType, pathUrl, result, entry.getName(), typeNames);
			}
		} catch (final IOException ioex) {
		}
	}

	private void doTypesFilter(final Class<?> baseType, final URL pathUrl, final Collection<String> result, final String clazzPath,
							   final String... typeNames) {
		if (!clazzPath.endsWith(".class"))
			return;
		final int lastDotIdx = clazzPath.lastIndexOf('.');
		if (-1 == lastDotIdx)
			return;
		final String typeDef = clazzPath.substring(0, lastDotIdx).replace('/', '.');
		if (null != typeNames && typeNames.length > 0) {
			final int lastDot = typeDef.lastIndexOf('.');
			if (lastDot == -1)
				return;
			final String typeName = typeDef.substring(lastDot + 1);
			boolean withLiked = false;
			for (final String tmpTypeName : typeNames) {
				if (!typeName.contains(tmpTypeName))
					continue;
				withLiked = true;
				break;
			}
			if (withLiked == false)
				return;
		}
		if (this.typeFilters.isEmpty()) {
			if (null == this.subTypeFilter || this.subTypeFilter.accept(baseType, pathUrl, clazzPath))
				result.add(typeDef);
		} else {
			for (final ITypeFilter typeFilter : this.typeFilters) {
				if (!typeFilter.accept(clazzPath))
					continue;
				if (null == this.subTypeFilter || this.subTypeFilter.accept(baseType, pathUrl, clazzPath))
					result.add(typeDef);
			}
		}
	}

	/**
	 * @param pathUrl
	 * @return
	 */
	private boolean acceptPathUrl(final URL pathUrl) {
		if (this.pathURLfilters.isEmpty())
			return true;
		for (final IPathURLFilter pathURLFilter : this.pathURLfilters) {
			if (pathURLFilter.accept(pathUrl))
				return true;
		}
		return false;
	}
}
