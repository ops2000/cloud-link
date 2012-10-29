package ali2012.link.spi.impl.http;

import java.util.LinkedHashMap;
import java.util.Map;

import ali2012.link.LinkFactory;
import ali2012.link.spi.LinkSource;
import ali2012.link.spi.LinkSourceProvider;
import ali2012.link.spi.impl.DefaultLinkSourceProvider;

/**
 * HttpURL获取互联网文件的SPI实现。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class HttpLinkSourceProvider extends DefaultLinkSourceProvider implements
		LinkSourceProvider {
	static {
		LinkFactory.register(new HttpLinkSourceProvider());
	}

	@Override
	public LinkSource getSource() {
		HttpLinkSource source = new HttpLinkSource(this);
		source.setProperties(getProperties());
		return source;
	}

	@Override
	public String getKey() {
		return "http";
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		return map;
	}

}
