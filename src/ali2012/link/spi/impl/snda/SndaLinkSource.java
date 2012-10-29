package ali2012.link.spi.impl.snda;

import java.net.URI;

import ali2012.link.LinkFactory;
import ali2012.link.api.LinkLocator;
import ali2012.link.api.LinkObject;
import ali2012.link.api.LinkObjectMetadata;
import ali2012.link.api.impl.OssLinkLocator;
import ali2012.link.spi.LinkSource;
import ali2012.link.spi.LinkSourceException;
import ali2012.link.spi.LinkSourceProvider;
import ali2012.link.spi.impl.DefaultLinkSource;

import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;
import com.snda.storage.SNDAStorage;
import com.snda.storage.SNDAStorageBuilder;

/**
 * 盛大云存储文件的SPI实现。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class SndaLinkSource extends DefaultLinkSource implements LinkSource {

	public SndaLinkSource(LinkSourceProvider provider) {
		super(provider);
	}

	@Override
	public LinkObject pull(URI uri) throws LinkSourceException {
		String appId = (String) properties.get("appId");
		String appKey = (String) properties.get("appKey");

		OssLinkLocator locator = (OssLinkLocator) LinkFactory.create(uri,
				this.getLinkLocatorClass());
		if (locator == null)
			throw new LinkSourceException("解析路径错误：" + uri.toString());

		SNDAStorage storage = new SNDAStorageBuilder()
				.credential(appId, appKey).bytesPerSecond(64 * 1024). // 限制每秒传输速率为64KB
				connectionTimeout(10 * 1000). // 设置ConnectionTimeout为10秒
				soTimeout(30 * 1000). // 设置SoTimeout为30秒
				build();

		SNDAObject object = null;
		try {
			object = storage.bucket(locator.getBucketName())
					.object(locator.getKey()).download();

			return new LinkObject(uri, object.getContent(),
					getMetadata(object.getObjectMetadata()));
		} catch (Exception e) {
			throw new LinkSourceException(e);
		}
	}

	private LinkObjectMetadata getMetadata(SNDAObjectMetadata ossObject) {
		if (ossObject == null)
			throw new java.lang.IllegalStateException("目前无法获取状态");

		LinkObjectMetadata meta = new LinkObjectMetadata();

		meta.setCacheControl(ossObject.getCacheControl());
		meta.setContentDisposition(ossObject.getContentDisposition());
		meta.setContentEncoding(ossObject.getContentEncoding());
		meta.setContentLength(ossObject.getContentLength());
		meta.setContentType(ossObject.getContentType());

		return meta;
	}

	@Override
	public Class<? extends LinkLocator<?>> getLinkLocatorClass() {
		return OssLinkLocator.class;
	}
}
