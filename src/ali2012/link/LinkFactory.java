package ali2012.link;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import ali2012.link.api.LinkLocator;
import ali2012.link.api.impl.OssLinkLocator;
import ali2012.link.spi.LinkSourceProvider;

/**
 * 面向SPI提供的简单工厂类，支持SPI实现者开发SPI提供程序。
 * <ul>
 * <li>建立和管理扩展的静态方法。SPI实现者，必须在静态代码块中注册扩展点
 * <li>建立LinkLocator对象的工厂方法
 * <ul>
 * 
 * @see ali2012.link.api.LinkLocator
 * 
 * @author wangs [ops2000@gmail.com]
 */
public class LinkFactory {
	private static Map<String, LinkSourceProvider> providers = new LinkedHashMap<String, LinkSourceProvider>();

	/**
	 * 注册一个ILinkSourceProvider扩展。
	 * 
	 * <p>
	 * 系统自建了三个扩展：Ali、http、snda
	 * 
	 * @param provider
	 *            ILinkSourceProvider实现
	 */
	public static void register(LinkSourceProvider provider) {
		if (provider == null || provider.getKey() == null)
			return;

		providers.put(provider.getKey(), provider);
	}

	/**
	 * 返回所有扩展
	 * 
	 * @return 扩展的集合
	 */
	public static Collection<LinkSourceProvider> listExtionsions() {
		return providers.values();
	}

	/**
	 * 通过字符串数组方式建立LinkLocator。实际上，目前只支持OssLinkLocator一种类型。
	 * 
	 * @param path
	 *            顺序的多个字符串片段
	 * @param clazz
	 *            LinkLocator子类，由{@link ali2012.link.spi.LinkSource}提供
	 * @return 创建失败返回null
	 * 
	 * @see ali2012.link.api.LinkLocator
	 * @see ali2012.link.spi.LinkSource
	 */
	public static <T extends LinkLocator<?>> LinkLocator<?> create(
			String[] path, Class<T> clazz) {
		if (OssLinkLocator.class.equals(clazz)) {
			return new OssLinkLocator(path);
		}
		return null;
	}

	/**
	 * 通过字符串数组方式建立LinkLocator。实际上，目前只支持OssLinkLocator一种类型。
	 * 
	 * @param path
	 *            URI
	 * @param clazz
	 *            LinkLocator子类，由{@link ali2012.link.spi.LinkSource}提供
	 * @return 创建失败返回null
	 * 
	 * @see ali2012.link.api.LinkLocator
	 * @see ali2012.link.spi.LinkSource
	 */
	public static <T extends LinkLocator<?>> LinkLocator<?> create(URI path,
			Class<T> clazz) {
		if (OssLinkLocator.class.equals(clazz)) {
			return new OssLinkLocator(path);
		}
		return null;
	}
}
