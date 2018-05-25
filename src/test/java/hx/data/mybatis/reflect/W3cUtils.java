package hx.data.mybatis.reflect;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * 使用W3C DOM操作XML的工具类
 *
 */
public abstract class W3cUtils {
	public static final String lineSeparator = System.getProperty("line.separator", "\n");

	// private static transient Object lockObj = new Object();
	public static final Document parseText(final String text) {
		try {
			return W3cUtils.readXmlDocument(new ByteArrayInputStream(text.getBytes("UTF-8")), false, "Parser from text");
		} catch (final Exception e) {
			e.printStackTrace();
			return W3cUtils.makeEmptyXmlDocument();
		}
	}

	public static void writeXmlDocument(final String filename, final Document document) throws FileNotFoundException, IOException {
		if (document == null) {
			return;
		}
		if (filename == null) {
			return;
		}

		final File outFile = new File(filename);
		if (!outFile.getParentFile().exists())
			outFile.getParentFile().mkdirs();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outFile);
			W3cUtils.writeXmlDocument(fos, document);
		} finally {
			if (null != fos)
				fos.close();
		}
	}

	public static void writeXmlDocument(final OutputStream os, final Document document) throws IOException {
		if (document == null) {
			return;
		}
		if (os == null) {
			return;
		}

		final OutputFormat format = new OutputFormat(document);
		format.setIndent(4);
		final XMLSerializer serializer = new XMLSerializer(os, format);
		serializer.asDOMSerializer();
		serializer.serialize(document.getDocumentElement());
	}

	public static Node adoptNode(final Document doc, final Node ele) {
		return doc.adoptNode(ele);
	}

	public static Document readXmlDocument(final String content) throws SAXException, ParserConfigurationException, IOException {
		return W3cUtils.readXmlDocument(content, false);
	}

	public static Document readXmlDocument(final String content, final boolean validate) throws SAXException, ParserConfigurationException,
			IOException {
		if (content == null) {
			return null;
		}
		final ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
		return W3cUtils.readXmlDocument(bis, validate, "Internal Content");
	}

	public static Document readXmlDocument(final URL url) throws SAXException, ParserConfigurationException, IOException {
		return W3cUtils.readXmlDocument(url, false);
	}

	public static Document readXmlDocument(final URL url, final boolean validate) throws SAXException, ParserConfigurationException, IOException {
		if (url == null) {
			return null;
		}
		return W3cUtils.readXmlDocument(url.openStream(), validate, url.toString());
	}

	public static final Document readXml(final String filePath) {
		try {
			return W3cUtils.readXmlDocument(new FileInputStream(filePath));
		} catch (final Exception e) {
			System.err.println("XmlUtils.readXml failure:" + e.getMessage());
		}
		return null;
	}

	public static Document readXmlDocument(final InputStream is) throws SAXException, ParserConfigurationException, IOException {
		return W3cUtils.readXmlDocument(is, false, null);
	}

	public static Document readXmlDocument(final InputStream is, final boolean validate, final String docDescription) throws SAXException,
			ParserConfigurationException, IOException {
		try {
			if (is == null)
				return null;
			Document document = null;
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validate);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
			return document;
		} finally {
			if (null != is)
				is.close();
		}
	}

	public static Document makeEmptyXmlDocument() {
		return W3cUtils.makeEmptyXmlDocument(null);
	}

	public static Document makeEmptyXmlDocument(final String rootElementName) {
		Document document = null;
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (final Exception e) {
		}
		if (rootElementName != null) {
			final Element rootElement = document.createElement(rootElementName);
			document.appendChild(rootElement);
		}
		if (document == null) {
			return null;
		}
		return document;
	}

	public static Element addChildElement(final Element element, final String childElementName) {
		final Element newElement = element.getOwnerDocument().createElement(childElementName);
		element.appendChild(newElement);
		return newElement;
	}

	public static void removeAllChild(final Element element) {
		final NodeList list = element.getChildNodes();
		for (final int i = 0; i < list.getLength();) {
			final Node node = list.item(i);
			element.removeChild(node);
		}
	}

	public static Element addChildElementValue(final Element element, final String childElementName, final String childElementValue) {
		final Element newElement = W3cUtils.addChildElement(element, childElementName);
		newElement.appendChild(element.getOwnerDocument().createTextNode(childElementValue));
		return newElement;
	}

	public static Element addChildElementCDATAValue(final Element element, final String childElementName, final String childElementValue) {
		final Element newElement = W3cUtils.addChildElement(element, childElementName);
		newElement.appendChild(element.getOwnerDocument().createCDATASection(childElementValue));
		return newElement;
	}

	public static List<Element> childElementList(final Element element, final String childElementName) {
		final List<Element> result = new ArrayList<Element>(8);
		if (element == null) {
			return result;
		}
		final NodeList list = null == childElementName ? element.getChildNodes() : element.getElementsByTagName(childElementName);
		for (int i = 0; i < list.getLength(); i++) {
			final Node node = list.item(i);
			if (node instanceof Element)
				result.add((Element) node);
		}
		return result;
	}

	public static Element firstChildElement(final Element element, final String childElementName) {
		if (element == null) {
			return null;
		}
		Node node = element.getFirstChild();
		if (node != null) {
			do {
				if (node.getNodeType() == Node.ELEMENT_NODE && (childElementName == null || childElementName.equals(node.getNodeName()))) {
					final Element childElement = (Element) node;
					return childElement;
				}
			} while ((node = node.getNextSibling()) != null);
		}
		return null;
	}

	public static Element firstChildElement(final Element element, final String childElementName, final String attrName, final String attrValue) {
		if (element == null) {
			return null;
		}
		Node node = element.getFirstChild();
		if (node != null) {
			do {
				if (node.getNodeType() == Node.ELEMENT_NODE && (childElementName == null || childElementName.equals(node.getNodeName()))) {
					final Element childElement = (Element) node;
					final String value = childElement.getAttribute(attrName);
					if (value != null && value.equals(attrValue)) {
						return childElement;
					}
				}
			} while ((node = node.getNextSibling()) != null);
		}
		return null;
	}

	public static String childElementValue(final Element element, final String childElementName) {
		if (element == null) {
			return null;
		}
		final Element childElement = W3cUtils.firstChildElement(element, childElementName);
		return W3cUtils.elementValue(childElement);
	}

	public static String elementValue(final Element element) {
		if (element == null) {
			return null;
		}
		element.normalize();
		final Node node = element.getFirstChild();
		if (node == null) {
			return null;
		}
		if (node instanceof CDATASection) {
			return ((CDATASection) node).getData();
		}
		return node.getNodeValue();
	}

	public static String getCDATAValue(final Element element) {
		if (element == null) {
			return null;
		}
		element.normalize();
		final NodeList nodel = element.getChildNodes();
		Node node = null;
		for (int i = 0; i < nodel.getLength(); i++) {
			node = nodel.item(i);
			if (node instanceof CDATASection) {
				return ((CDATASection) node).getData();
			}
		}
		return null;
	}

	public static Element firstChildElement(final Element element, final String attrName, final String attrValue) {
		if (element == null) {
			return null;
		}
		Node node = element.getFirstChild();
		if (node != null) {
			do {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					final Element childElement = (Element) node;
					final String value = childElement.getAttribute(attrName);
					if (value != null && value.equalsIgnoreCase(attrValue)) {
						return childElement;
					}
				}
			} while ((node = node.getNextSibling()) != null);
		}
		return null;
	}

	public final static byte[] translateXML(final InputStream xml, final InputStream xsl) throws TransformerConfigurationException,
			TransformerException {
		final ByteArrayOutputStream ret = new ByteArrayOutputStream();
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Source xmlSource = new StreamSource(xml);
		final Source xslSource = new StreamSource(xsl);
		final Templates template = tf.newTemplates(xslSource);
		final Transformer transformer = template.newTransformer();
		transformer.transform(xmlSource, new StreamResult(ret));
		return ret.toByteArray();
	}

	public static Document newDom(final String rootEleText) {
		return W3cUtils.makeEmptyXmlDocument(rootEleText);
	}

	public static final Element addEle(final Element parentNode, final String tagName) {
		final Element ele = parentNode.getOwnerDocument().createElement(tagName);
		parentNode.appendChild(ele);
		return ele;
	}

	public static final void clear(final Node node) {
		if (null == node)
			return;
		final NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			node.removeChild(nodes.item(i--));
		}
	}

	private static String normalize(final String s) {
		final StringBuffer stringbuffer = new StringBuffer();
		final int i = s == null ? 0 : s.length();
		for (int j = 0; j < i; j++) {
			final char c = s.charAt(j);
			switch (c) {
				case 60: // '<'
					stringbuffer.append("&lt;");
					break;

				case 62: // '>'
					stringbuffer.append("&gt;");
					break;

				case 38: // '&'
					stringbuffer.append("&amp;");
					break;

				case 34: // '"'
					stringbuffer.append("&quot;");
					break;

				case 10: // '\n'
					if (j > 0) {
						final char c1 = stringbuffer.charAt(stringbuffer.length() - 1);
						if (c1 != '\r') {
							stringbuffer.append(W3cUtils.lineSeparator);
						} else {
							stringbuffer.append('\n');
						}
					} else {
						stringbuffer.append(W3cUtils.lineSeparator);
					}
					break;

				default:
					stringbuffer.append(c);
					break;
			}
		}

		return stringbuffer.toString();
	}

	private static void print(final Node node, final PrintWriter printwriter) {
		if (node == null) {
			return;
		}
		boolean flag = false;
		final short word0 = node.getNodeType();
		switch (word0) {
			case 9: // '\t'
				printwriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				final NodeList nodelist = node.getChildNodes();
				if (nodelist != null) {
					final int i = nodelist.getLength();
					for (int k = 0; k < i; k++) {
						W3cUtils.print(nodelist.item(k), printwriter);

					}
				}
				break;

			case 1: // '\001'
				printwriter.print('<' + node.getNodeName());
				final NamedNodeMap namednodemap = node.getAttributes();
				final int j = namednodemap == null ? 0 : namednodemap.getLength();
				for (int l = 0; l < j; l++) {
					final Attr attr = (Attr) namednodemap.item(l);
					printwriter.print(' ' + attr.getNodeName() + "=\"" + W3cUtils.normalize(attr.getValue()) + '"');
				}

				final NodeList nodelist1 = node.getChildNodes();
				if (nodelist1 != null) {
					final int i1 = nodelist1.getLength();
					flag = i1 > 0;
					if (flag) {
						printwriter.print('>');
					}
					for (int j1 = 0; j1 < i1; j1++) {
						W3cUtils.print(nodelist1.item(j1), printwriter);

					}
				} else {
					flag = false;
				}
				if (!flag) {
					printwriter.println("/>");
				}
				break;

			case 5: // '\005'
				printwriter.print('&');
				printwriter.print(node.getNodeName());
				printwriter.print(';');
				break;

			case 4: // '\004'
				printwriter.print("<![CDATA[");
				printwriter.print(node.getNodeValue());
				printwriter.println("]]>");
				break;

			case 3: // '\003'
				printwriter.print(W3cUtils.normalize(node.getNodeValue()));
				break;

			case 8: // '\b'
				printwriter.print("<!--");
				printwriter.print(node.getNodeValue());
				printwriter.println("-->");
				break;

			case 7: // '\007'
				printwriter.print("<?");
				printwriter.print(node.getNodeName());
				final String s = node.getNodeValue();
				if (s != null && s.length() > 0) {
					printwriter.print(' ');
					printwriter.print(s);
				}
				printwriter.println("?>");
				break;
		}
		if (word0 == 1 && flag) {
			printwriter.print("</");
			printwriter.print(node.getNodeName());
			printwriter.println('>');
			// boolean flag1 = false;
		}
	}

	public static final String asXml(final Node node) {
		final StringWriter stringwriter = new StringWriter();
		W3cUtils.asXml(node, stringwriter);
		return stringwriter.toString();
	}

	public static final void asXml(final Node node, final Writer writer) {
		W3cUtils.print(node, new PrintWriter(writer));
	}

	public static final String asXml(final Node node, final String charSet) {
		final String str = W3cUtils.asXml(node);
		try {
			return new String(str.getBytes(charSet));
			// return new String(str.getBytes("UTF-8"), charSet);
		} catch (final UnsupportedEncodingException e) {
			return str;
		}
	}

	public static final String safeGetText(final Element element) {
		if (null == element)
			return null;
		String text = element.getAttribute("value");
		String tmpTxt;
		if (null == text || "".equals(text)) {
			tmpTxt = element.getTextContent();
			if (null != tmpTxt)
				text = tmpTxt;
		}
		return null != text ? text.trim() : null;
	}

	/**
	 * 添加Element并使用Text存储数据格式,添加前将移除已经存在的定义
	 *
	 * @param parentNode
	 * @param name
	 * @param value
	 */
	public static final void addEle(final Element parentNode, final String tagName, final String name, final Object value) {
		final Element oldEle = W3cUtils.firstChildElement(parentNode, tagName, "name", name);
		if (null != oldEle)
			parentNode.removeChild(oldEle);
		final Element ele = W3cUtils.addEle(parentNode, tagName);
		ele.setAttribute("name", name);
		ele.appendChild(parentNode.getOwnerDocument().createTextNode(String.valueOf(value)));
	}

	/**
	 * 添加Element并使用Text存储数据格式,添加前将移除已经存在的定义
	 *
	 * @param parentNode
	 * @param name
	 * @param value
	 */
	public static final void addEle(final Element parentNode, final String tagName, final Object value) {
		final Element oldEle = W3cUtils.firstChildElement(parentNode, tagName);
		if (null != oldEle)
			parentNode.removeChild(oldEle);
		final Element ele = W3cUtils.addEle(parentNode, tagName);
		ele.appendChild(parentNode.getOwnerDocument().createTextNode(String.valueOf(value)));
	}
}
