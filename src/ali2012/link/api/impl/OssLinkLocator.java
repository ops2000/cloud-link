package ali2012.link.api.impl;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ali2012.link.api.LinkLocator;
import ali2012.link.api.impl.ali.OssObjectX;

/**
 * 针对类似Bucket和Object Key方式定位器实现。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class OssLinkLocator extends LinkLocator<OssObjectX> {
	protected static final Log log = LogFactory.getLog(OssLinkLocator.class);

	public static final String OSS_SERNAME = "storage.aliyun.com";

	private String bucketName;
	private String key;
	private String serverName = OSS_SERNAME;

	public OssLinkLocator(String... path) {
		super(path);
	}

	public OssLinkLocator(URI path) {
		super(path);
	}

	@Override
	protected OssObjectX parseUri(URI path) {
		String uriPath = path.getPath();
		String[] all = StringUtils.split(uriPath, '/');
		if (all.length < 2)
			return null;
		bucketName = all[0];
		key = StringUtils.join(all, '/', 1, all.length);
		serverName = path.getHost();
		if (log.isTraceEnabled())
			log.trace("serverName:" + serverName);

		return new OssObjectX(bucketName, key);
	}

	@Override
	protected OssObjectX parseStringArray(String[] path) {
		bucketName = path[0];
		key = path[1];

		if (path.length > 2) {
			serverName = path[2] == null ? serverName : path[2];
		}

		return new OssObjectX(bucketName, key);
	}

	@Override
	public URI getPathUri() {
		return URI.create("ali://" + serverName + "/" + bucketName + "/" + key);
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getKey() {
		return key;
	}

	public String getServerName() {
		return serverName;
	}
}
