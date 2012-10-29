package ali2012.link.spi.impl.http;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import ali2012.link.api.LinkLocator;
import ali2012.link.api.LinkObject;
import ali2012.link.api.LinkObjectMetadata;
import ali2012.link.spi.LinkSource;
import ali2012.link.spi.LinkSourceException;
import ali2012.link.spi.LinkSourceProvider;
import ali2012.link.spi.impl.DefaultLinkSource;

/**
 * HttpURL获取互联网文件的SPI实现。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class HttpLinkSource extends DefaultLinkSource implements LinkSource {

	public HttpLinkSource(LinkSourceProvider provider) {
		super(provider);
	}

	@Override
	public LinkObject pull(URI uri) throws LinkSourceException {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = httpclient.execute(httpget);

			StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == 200) {
				LinkObject linkObjcet = new LinkObject(uri, IOUtils.toBufferedInputStream(response
						.getEntity().getContent()),
						buildLinkOjbectMetadata(response));

				return linkObjcet;
			} else {
				throw new RuntimeException("文件获取失败："
						+ statusLine.getReasonPhrase());
			}
		} catch (Exception e) {
			throw new RuntimeException("未知错误", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	protected LinkObjectMetadata buildLinkOjbectMetadata(HttpResponse response) {
		LinkObjectMetadata meta = new LinkObjectMetadata();
		HttpEntity entity = response.getEntity();
		meta.setContentLength(entity.getContentLength());
		if (entity.getContentEncoding() != null)
			meta.setContentEncoding(entity.getContentEncoding().getValue());
		if (entity.getContentType() != null)
			meta.setContentType(entity.getContentType().getValue());

		return meta;
	}

	@Override
	public Class<? extends LinkLocator<?>> getLinkLocatorClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
