package ali2012.link.spi.impl;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ali2012.link.api.LinkLocator;
import ali2012.link.api.LinkObjectMetadata;
import ali2012.link.spi.LinkSource;
import ali2012.link.spi.LinkSourceProvider;

/**
 * SPI扩展LinkSource的默认实现。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public abstract class DefaultLinkSource implements LinkSource {
	protected final Log log = LogFactory.getLog(DefaultLinkSource.class);

	protected Map<String, Object> properties;
	protected LinkObjectMetadata meta;
	protected LinkSourceProvider provider;
	protected Class<? extends LinkLocator<?>> locatorClass;

	public DefaultLinkSource(LinkSourceProvider provider) {
		this.provider = provider;
		properties = new LinkedHashMap<String, Object>();

		meta = new LinkObjectMetadata();
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		if (properties == null)
			return;
		this.properties.putAll(properties);
	}

	@Override
	public boolean isUpdated(URI uri, LinkObjectMetadata meta) {
		return false;
	}

	public Map<String, Object> getGlobalProperties() {
		return provider.getProperties();
	}
}
