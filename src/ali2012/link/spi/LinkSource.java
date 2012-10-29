package ali2012.link.spi;

import java.net.URI;
import java.util.Map;

import ali2012.link.api.LinkLocator;
import ali2012.link.api.LinkObject;
import ali2012.link.api.LinkObjectMetadata;

/**
 * 外部资源内容获取接口。这是SPI必须实现的两个接口之一。
 * 
 * <p>
 * 实现{@link #full(URI)}方法，完成外部资源读取功能。{@link #setProperties(Map)}为整个数据读取提供配置参数。
 * 
 * <p>
 * 对于采用映射联接方式的外部资源，需要通过{@link #isUpdated(URI, LinkObjectMetadata)}
 * 判断外部资源是否已经更新，一般应尽可能有效率的实现该方法。
 * 
 * <p>
 * 如果采用了特殊的外部资源定位规则，可覆盖getLinkLocatorClass，提供自定义的getLinkLocator类。该类根据规则转换URI、
 * 字符串片段等。
 * 
 * @author wangs [ops2000@gmail.com]
 * 
 */
public interface LinkSource {
	/**
	 * 资源定位转换器
	 */
	public Class<? extends LinkLocator<?>> getLinkLocatorClass();

	/**
	 * 设置Source配置属性
	 * 
	 * @param properties
	 */
	public void setProperties(Map<String, Object> properties);

	/**
	 * 拉取外部的资源
	 * 
	 * @param uri
	 *            外部资源的URI
	 * @return LinkObject
	 * @throws LinkSourceException
	 */
	public LinkObject pull(URI uri) throws LinkSourceException;

	/**
	 * 是否更新。该方法应尽量高效实效。比如通过对资源的更新时间比对返回是否更新。
	 * 
	 * @param uri 外部地址
	 * @param meta 当前外部对象的元信息
	 * @return 外部资源是否已更新
	 */
	public boolean isUpdated(URI uri, LinkObjectMetadata meta);
}
