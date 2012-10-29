package ali2012.link.spi;

import java.net.URI;
import java.util.Map;

/**
 * 提供 LinkSource、配置、协议支持判断等功能。这是SPI必须实现的两个接口之一。
 * 
 * 通过{@link ali2012.link.LinkFactory#register(LinkSourceProvider)} 注册
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public interface LinkSourceProvider {
	/**
	 * 获取配置属性，这个是全局的配置参数
	 * @return
	 */
	public Map<String, Object> getProperties();
	
	/**
	 * 获取源处理程序
	 * @return
	 */
	public LinkSource getSource();
	
	/**
	 * 是否支持一个URI
	 * @param uri
	 * @return
	 */
	public boolean support(URI uri);
	
	/**
	 * 获得唯一标识
	 * @param uri
	 * @return
	 */
	public String getKey();
}
