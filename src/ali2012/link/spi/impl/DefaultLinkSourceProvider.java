package ali2012.link.spi.impl;

import java.net.URI;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ali2012.link.spi.LinkSourceProvider;

/**
 * SPI扩展LinkSourceProvider的默认实现。
 * @author wangs [ops2000@gmail.com]
 *
 */
public abstract class DefaultLinkSourceProvider implements LinkSourceProvider {
	protected final Log log = LogFactory.getLog(DefaultLinkSourceProvider.class);
	
	@Override
	public Map<String, Object> getProperties() {
		return null;
	}

	@Override
	public boolean support(URI uri) {
		if (uri == null)
			return false;

		String s = uri.getScheme();
		if (s == null)
			return false;

		return s.equalsIgnoreCase(getKey());
	}
}
