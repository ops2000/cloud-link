package ali2012.link.spi.impl.ali;

import java.net.URI;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import ali2012.link.LinkFactory;
import ali2012.link.api.LinkLocator;
import ali2012.link.api.LinkObject;
import ali2012.link.api.LinkObjectMetadata;
import ali2012.link.api.impl.OssLinkLocator;
import ali2012.link.api.impl.ali.OssClientX;
import ali2012.link.spi.LinkSource;
import ali2012.link.spi.LinkSourceException;
import ali2012.link.spi.LinkSourceProvider;
import ali2012.link.spi.impl.DefaultLinkSource;

import com.aliyun.openservices.ClientConfiguration;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;

/**
 * 阿里云OSS的SPI实现。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class AliLinkSource extends DefaultLinkSource implements LinkSource {

	public AliLinkSource(LinkSourceProvider provider) {
		super(provider);
	}

	@Override
	public LinkObject pull(URI uri) throws LinkSourceException {
		String appId = (String) properties.get("appId");
		String appKey = (String) properties.get("appKey");
		String endPoint = (String) properties.get("endPoint");

		ClientConfiguration clientConfig = new ClientConfiguration();
		OssClientX client = new OssClientX(endPoint, appId, appKey,
				clientConfig);
		OSSObject ossObject;

		log.info("开始获取连接对象。URI=" + uri.toString());

		// 解析uri，获取bucket和key
		OssLinkLocator locator = (OssLinkLocator)LinkFactory.create(uri, this.getLinkLocatorClass());
		if (locator == null)
			throw new LinkSourceException("解析路径错误：" + uri.toString());
		try {
			ossObject = client.getLawObject(locator.getBucketName(), locator.getKey());
			LinkObjectMetadata metadata = getMetadata(ossObject);

			// 不是云联文件，直接返回对象内容
			String version = (String) metadata.getUserMetadata().get("yunlink");
			if (StringUtils.isBlank(version)) { // 不是云联文件
				log.info("不是云联对象，直接返回内容");
				return new LinkObject(uri, ossObject.getObjectContent(),
						metadata);
			}

			// 检测连接类型
			boolean pyhsics = BooleanUtils.toBoolean(metadata.getUserMetadata()
					.get("pyhsics"));
			if (pyhsics) { // 如果是硬链接判断是否需更新
				log.debug("物理连接方式");
				if (this.isUpdated(uri, metadata)) {
					log.debug("原地址已经更新，先使用映射方式获取最新内容");
					// TODO 更新连接，异步进行

					pyhsics = false; // 这里异步，下面先从远处获取，各行其是，两不耽误
				} else {
					log.info("使用硬连接方式返回文件内容");
					return new LinkObject(uri, ossObject.getObjectContent(),
							metadata, pyhsics); // 硬连接获取
				}
			}

			if (!pyhsics) { // 如果映射连接，获取实际内容
				String strUri = metadata.getUserMetadata().get("refer");
				log.info("使用映射连接方式获取文件内容:" + strUri);

				try {
					ossObject = client.getObject(strUri);
				} catch (Exception e) {
					log.warn("使用映射方式获取strUri内容失败，可能由于外部地址失效引起");
				}
				return new LinkObject(uri, ossObject.getObjectContent(),
						metadata, pyhsics); // 软连接获取
			}

			throw new LinkSourceException("未知状态异常");

		} catch (Exception e) {
			throw new LinkSourceException(e);
		} finally {
			log.info("连接对象获取完毕。URI=" + uri.toString());
		}
	}

	private LinkObjectMetadata getMetadata(OSSObject ossObject) {
		if (ossObject == null)
			throw new java.lang.IllegalStateException("目前无法获取状态");

		ObjectMetadata originMeta = ossObject.getObjectMetadata();

		LinkObjectMetadata meta = new LinkObjectMetadata();

		meta.setCacheControl(originMeta.getCacheControl());
		meta.setContentDisposition(originMeta.getContentDisposition());
		meta.setContentEncoding(originMeta.getContentEncoding());
		meta.setContentLength(originMeta.getContentLength());
		meta.setContentType(originMeta.getContentType());
		meta.setExpirationTime(originMeta.getExpirationTime());
		meta.setLastModified(originMeta.getLastModified());
		meta.setUserMetadata(originMeta.getUserMetadata());

		return meta;
	}

	@Override
	public Class<? extends LinkLocator<?>> getLinkLocatorClass() {
		return OssLinkLocator.class;
	}
}
