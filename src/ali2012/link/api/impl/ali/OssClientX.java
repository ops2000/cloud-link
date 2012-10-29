package ali2012.link.api.impl.ali;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ali2012.link.LinkFactory;
import ali2012.link.api.Link;
import ali2012.link.api.LinkException;
import ali2012.link.api.LinkLocator;
import ali2012.link.api.LinkObject;
import ali2012.link.api.impl.OssLinkLocator;
import ali2012.link.spi.LinkSource;
import ali2012.link.spi.LinkSourceException;
import ali2012.link.spi.LinkSourceProvider;

import com.aliyun.openservices.ClientConfiguration;
import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.GetObjectRequest;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;

/**
 * 阿里云OSS的扩展，扩展了{@link com.aliyun.openservices.oss.OSSClient OSSClient}。
 * 
 * <p>
 * 实现了{@link ali2012.link.api.Link Link}
 * 接口，开发者可以通过此类中的方法快速实现外部资源的联接，同时也提供了URI快速获取各类资源（包括阿里云OSS对象）的功能。
 * 
 * <p>
 * 定义了{@link #getLawObject(String,String)}方法。可以直接访问原生的获取Object的方法
 * 
 * @author wangs [ops2000@gmail.com]
 * 
 */
public class OssClientX extends OSSClient implements Link<OssObjectX> {
	protected final Log log = LogFactory.getLog(OssClientX.class);

	public OssClientX(String bucketName, String key, String arg2,
			ClientConfiguration arg3) {
		super(bucketName, key, arg2, arg3);
	}

	public OssClientX(String bucketName, String key, String arg2) {
		super(bucketName, key, arg2);
	}

	public OssClientX(String bucketName, String key) {
		super(bucketName, key);
	}

	public OSSObject getLawObject(String bucketName, String key)
			throws OSSException, ClientException {
		return super.getObject(new GetObjectRequest(bucketName, key));
	}

	@Override
	public void link(URI referObjectUri, LinkLocator<OssObjectX> locator)
			throws LinkException, LinkSourceException {
		link(referObjectUri, locator, locator.isPhysicsDefault());
	}

	@Override
	public void link(String referObjectPath, LinkLocator<OssObjectX> locator)
			throws LinkException, LinkSourceException {
		link(referObjectPath, locator, locator.isPhysicsDefault());
	}

	@Override
	public void link(URI referObjectUri, LinkLocator<OssObjectX> locator,
			boolean isPyhsics) throws LinkException, LinkSourceException {
		InputStream content = null;
		ObjectMetadata meta = null;

		if (isPyhsics) { // 如果硬连接
			log.info("使用硬连接方式");

			// 获得sourceObject
			OssObjectX ossObject = getObject(referObjectUri);

			ObjectMetadata tmpMeta = ossObject.getObjectMetadata();
			meta = new ObjectMetadata();
			meta.setContentLength(tmpMeta.getContentLength());
			meta.setContentType("ali/yunlink");

			mark(meta, referObjectUri, true);

			content = ossObject.getObjectContent();

		} else { // 如果是软连接，建立一个LinkObject，内容指向实际地址
			log.info("使用映射连接方式");

			URI targetUri = locator.getPathUri();
			if (referObjectUri.equals(targetUri))
				throw new LinkSourceException("不能建立自引用对象的循环连接");

			String uri = referObjectUri.toString();
			log.debug("映射连接：" + uri);

			content = IOUtils.toInputStream(uri); // 根据设计此处建立空文件，但目前不支持空内容的文件建立

			meta = new ObjectMetadata();
			meta.setContentLength(uri.length());
			meta.setContentType("ali/yunlink");
			mark(meta, referObjectUri, false);
		}

		OssObjectX path = locator.getPath();
		try {
			putObject(path.getBucketName(), path.getKey(), content, meta);// TODO
		} catch (Exception e) {
			throw new LinkSourceException(e);
		}
	}

	@Override
	public void link(String referObjectPath, LinkLocator<OssObjectX> locator,
			boolean isPyhsics) throws LinkException, LinkSourceException {
		URI uri = URI.create(referObjectPath);

		link(uri, locator, isPyhsics);
	}

	@Override
	public OssObjectX getObject(String objectPath) throws LinkException,
			LinkSourceException {
		return getObject(URI.create(objectPath));
	}

	@Override
	public OssObjectX getObject(URI objectUri) throws LinkException,
			LinkSourceException {
		if (objectUri == null)
			throw new java.lang.IllegalArgumentException();

		OssObjectX ossObject = null;

		boolean hasSupport = false;

		for (LinkSourceProvider provider : LinkFactory.listExtionsions()) {
			if (provider.support(objectUri)) {
				hasSupport = true;

				LinkSource source = provider.getSource();

				ossObject = new OssObjectX();
				try {
					LinkObject link = source.pull(objectUri);
					ossObject.setObjectContent(link.getContent());
					ossObject.setObjectMetadata(link.getMetadata());
				} catch (LinkSourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break; // 当前只支持一个匹配
			}
		}

		if (!hasSupport)
			throw new LinkSourceException("不支持的URI scheme.URI=" + objectUri);

		return ossObject;
	}

	public ObjectMetadata getObject(URI uri, File targetFile)
			throws OSSException, ClientException {
		OssObjectX ossObject;
		try {
			ossObject = getObject(uri);
		} catch (Exception e) {
			throw new ClientException(e);
		}

		try {
			IOUtils.copy(ossObject.getObjectContent(), new FileOutputStream(
					targetFile));
		} catch (FileNotFoundException e) {
			throw new ClientException(e);
		} catch (IOException e) {
			throw new ClientException(e);
		}

		return ossObject.getObjectMetadata();
	}

	@Override
	public ObjectMetadata getObject(GetObjectRequest request, File targetFile)
			throws OSSException, ClientException {
		OssLinkLocator locator = new OssLinkLocator(request.getBucketName(),
				request.getKey());
		return getObject(locator.getPathUri(), targetFile);

	}

	@Override
	public OssObjectX getObject(GetObjectRequest request) throws OSSException,
			ClientException {
		try {
			OssLinkLocator locator = new OssLinkLocator(
					request.getBucketName(), request.getKey());

			return getObject(locator.getPathUri());
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public OssObjectX getObject(String bucketName, String key)
			throws OSSException, ClientException {
		try {
			OssLinkLocator locator = new OssLinkLocator(bucketName, key);
			return getObject(locator.getPathUri());
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	/**
	 * 添加OSS Object meta信息，区分是否云联扩展
	 * @param objectMeta
	 * @param referUri
	 * @param isPyhsics
	 */
	void mark(ObjectMetadata objectMeta, URI referUri, boolean isPyhsics) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("yunlink", "1.0");
		param.put("refer", referUri.toString());
		param.put("pyhsics", Boolean.toString(isPyhsics));
		objectMeta.setUserMetadata(param);
	}
}