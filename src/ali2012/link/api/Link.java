package ali2012.link.api;

import java.net.URI;


import ali2012.link.spi.LinkSourceException;

/**
 * 定义需要扩展的中心OSS（阿里云OSS）的调用服务接口。
 * 
 * 主要分两类：
 * 
 * <ul>
 * <li><tt>link</tt>：指定联接方式和不指定两类，每一类，需要提供外部资源的URI和内部云对象的定位
 * <li><tt>getObject</tt>：通过URI统一定位获取云对象
 * </ul>
 * 
 * @param <T>
 *            getObject获取对象类型
 * 
 * @author wangs [ops2000@gmail.com]
 */
public interface Link<T> {
	/**
	 * 指定外部资源地址和本地定位建立联接。使用默认方式联接资源，默认方式取决于LinkSource
	 * 
	 * @param referObjectUri
	 *            外部资源地址
	 * @param locator
	 *            本地定位
	 * @throws LinkException
	 * @throws LinkSourceException
	 */
	public void link(URI referObjectUri, LinkLocator<T> locator)
			throws LinkException, LinkSourceException;

	/**
	 * 等同{@link #link(URI, LinkLocator)}，提供外部资源地址的字符类型参数
	 * 
	 * @param referObjectUri
	 *            外部资源地址
	 * @param locator
	 *            本地定位
	 * @throws LinkSourceException
	 * @throws LinkException
	 */
	public void link(String referObjectPath, LinkLocator<T> locator)
			throws LinkException, LinkSourceException;

	/**
	 * 等同{@link #link(URI, LinkLocator)}，但需要指定联接方式
	 * 
	 * @param referObjectUri
	 * @param locator
	 * @param isPyhsics
	 * @throws LinkException
	 * @throws LinkSourceException
	 */
	public void link(URI referObjectUri, LinkLocator<T> locator,
			boolean isPyhsics) throws LinkException, LinkSourceException;

	/**
	 * 等同{@link #link(URI, LinkLocator, boolean)}，提供外部资源地址的字符类型参数
	 * 
	 * @param referObjectUri
	 * @param locator
	 * @param isPyhsics
	 * @throws LinkException
	 * @throws LinkSourceException
	 */
	public void link(String referObjectPath, LinkLocator<T> locator,
			boolean isPyhsics) throws LinkException, LinkSourceException;

	/**
	 * 
	 * @param objectPath
	 * @return
	 * @throws LinkException
	 * @throws LinkSourceException
	 */
	public T getObject(String objectPath) throws LinkException,
			LinkSourceException;

	/**
	 * 统一资源的获取方式。目前系统提供了<code>ali:\\</code>、<code>http:\\</code>、
	 * <code>snda:\\</code>三种资源定位器。
	 * <p>
	 * 实现ali2012.link.spi包中的类，可以提供任意外部资源获取
	 * 
	 * @param objectUri
	 * @return
	 * @throws LinkException
	 * @throws LinkSourceException
	 */
	public T getObject(URI objectUri) throws LinkException,
			LinkSourceException;
}
