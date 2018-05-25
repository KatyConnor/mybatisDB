package hx.data.mybatis.reflect;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Some classpath convenient methods
 */
public abstract class ClasspathHelper {

	/**
	 * urls in current classpath from System property java.class.path
	 */
	public static Collection<URL> getUrlsForCurrentClasspath() {
		final Collection<URL> urls = new ArrayList<URL>(32);
		final String javaClassPath = System.getProperty("java.class.path");
		if (javaClassPath != null) {
			final String[] pathItems = javaClassPath.split(File.pathSeparator);
			for (final String path : pathItems) {
				try {
					final URL url = new File(path).toURI().toURL();
					urls.add(normalize(url));
				} catch (final MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}
		}
		mergeURLS(Thread.currentThread().getContextClassLoader(), urls);
		mergeURLS(System.class.getClassLoader(), urls);

		return urls;
	}

	public static final void mergeURLS(final ClassLoader classLoader, final Collection<URL> urls) {
		if (!(classLoader instanceof URLClassLoader))
			return;
		final URLClassLoader ucl = (URLClassLoader) classLoader;
		final URL urlList[] = ucl.getURLs();
		for (final URL url : urlList) {
			if (urls.contains(url))
				continue;
			urls.add(url);
		}
	}

	// todo: this is only partial, probably
	public static URL normalize(URL url) throws MalformedURLException {
		String spec = url.getFile();

		// get url base - remove everything after ".jar!/??" , if exists
		final int i = spec.indexOf("!/");
		if (i != -1) {
			spec = spec.substring(0, spec.indexOf("!/"));
		}

		// uppercase windows drive
		url = new URL(url, spec);
		final String file = url.getFile();
		final int i1 = file.indexOf(":");
		if (i1 != -1) {
			final String drive = file.substring(i1 - 1, 2).toUpperCase();
			url = new URL(url, file.substring(0, i1 - 1) + drive + file.substring(i1));
		}

		return url;
	}
}
