package ali2012.link.spi.impl.snda;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import ali2012.link.LinkFactory;
import ali2012.link.spi.LinkSource;
import ali2012.link.spi.LinkSourceProvider;
import ali2012.link.spi.impl.DefaultLinkSourceProvider;

/**
 * 盛大云存储文件的SPI实现。
 *  
 * @author wangs [ops2000@gmail.com]
 *
 */
public class SndaLinkSourceProvider extends DefaultLinkSourceProvider implements
		LinkSourceProvider {
	static {
		LinkFactory.register(new SndaLinkSourceProvider());
	}
	

	@Override
	public LinkSource getSource() {
		SndaLinkSource source = new SndaLinkSource(this);
		source.setProperties(getProperties());
		return source;
	}

	@Override
	public String getKey() {
		return "snda";
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Properties p = new Properties();
		try {
			p.load(this.getClass().getResourceAsStream("oss.properties"));
		} catch (IOException e) {
			log.warn("无法获取OSS提供器的配置文件");
		}

		for (Entry<Object, Object> entry : p.entrySet()) {
			map.put((String) entry.getKey(), entry.getValue());
		}
		return map;
	}

}
