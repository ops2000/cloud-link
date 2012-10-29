package ali2012.link.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import ali2012.link.LinkFactory;
import ali2012.link.api.impl.OssLinkLocator;
import ali2012.link.api.impl.ali.OssClientX;
import ali2012.link.spi.LinkSourceProvider;

import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.OSSObject;

/**
 * 各种应用场景的示例和测试。主要演示和测试的功能包括：
 * <ul>
 * <li>直接获取外部资源，使用扩展的{@link ali2012.link.api.impl.ali.OssClientX#getObject(java.net.URI) getObject(java.net.URI)}
 * <li>联接并获取阿里云OSS的其他对象
 * <li>联接并获取Web文件资源
 * <li>联接并获取第三方的云存储资源
 * <li>每次联接测试，同时测试硬联接和映射联接两种联接方式
 * <li>显示当前已经实现的SPI扩展插件
 * </ul>
 * 
 * @author wangs [ops2000@gmail.com]
 * 
 */
public class Sample {
	protected static final Log log = LogFactory.getLog(Sample.class);

	// 程序配置常量
	private static final String ACCESS_ID = "29fmlixpkpql485mv7yrrir7";
	private static final String ACCESS_KEY = "/BHdyYSa6PXbUP+Y517YyjhxsVw=";
	private static final String OSS_ENDPOINT = "http://storage.aliyun.com/";
	private static final String DEFAULT_BUCKET = "yunlink";

	// 阿里云文件地址，（请确保对象存在）
	private static final String ALI_OBJECT_URI = "ali://storage.aliyun.com/origin/sample";

	// 网站常量
	private static final String WEB_FILE_URL = "http://static.aliyun.com/images/www-summerwind/logo.gif";
	private static final String WEB_FILE_SAVETO = "c:\\ali-logo.gif";

	// 盛大云文件地址，（请确保对象存在）
	private static final String SNDA_OBJECT_URI = "snda://*/ali-snda/sample.jpg";

	@BeforeClass
	public static void loadClass() throws Exception {
		log.info("注册已经实现的三项扩展");
		
		Class.forName("ali2012.link.spi.impl.ali.AliLinkSourceProvider");
		Class.forName("ali2012.link.spi.impl.http.HttpLinkSourceProvider");
		Class.forName("ali2012.link.spi.impl.snda.SndaLinkSourceProvider");
	}
	
	/**
	 * 阿里云存储内部对象link到不同的bucket中，并通过URI统一获取。
	 * 
	 * <p>每个测试都提供三种方式联接：硬连接、映射联接和默认联接。
	 * 
	 * @throws ClientException
	 * @throws OSSException
	 * @throws IOException
	 */
	@Test
	public void aliOssLink() throws Exception {
		String destKey = "sample";

		linkTest(ALI_OBJECT_URI, DEFAULT_BUCKET, destKey);
	}

	/**
	 * 和Web文件link到阿里云中，并通过URI获取
	 * 
	 * <p>每个测试都提供三种方式联接：硬连接、映射联接和默认联接。
	 */
	@Test
	public void httpLink() throws Exception {
		String destKey = "sample2";

		linkTest(WEB_FILE_URL, DEFAULT_BUCKET, destKey);
	}

	/**
	 * 将一个互联网图片和阿里云建立连接，然后通过阿里云地址直接获取图片文件，并保存在本地硬盘
	 */
	@Test
	public void httpLink2() throws Exception {
		String destKey = "sample2a";

		OssClientX clientX = new OssClientX(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);

		OssLinkLocator locator = new OssLinkLocator(DEFAULT_BUCKET, destKey);

		// 建立Web图片和阿里云地址关联
		clientX.link(WEB_FILE_URL, locator, false);

		// 通过阿里云地址获取图片
		clientX.getObject(locator.getPathUri(), new File(WEB_FILE_SAVETO));

	}

	/**
	 * 将第三方云存储（盛大）对象link到阿里云中，并通过URI获取。
	 * 
	 * <p>每个测试都提供三种方式联接：硬连接、映射联接和默认联接。
	 */
	@Test
	public void sndaLink() throws Exception {
		String destKey = "snda.jpg";
		try {
			linkTest(SNDA_OBJECT_URI, DEFAULT_BUCKET, destKey);
		} catch (Exception ex) {
			fail("可能由于盛大账号被冻结造成，请修改/cloud-link/src/ali2012/link/spi/impl/snda/oss.properties中的值");
		}
	}

	/**
	 * 列出已有扩展
	 */
	@Test
	public void listExtendsion() {
		for (LinkSourceProvider provider : LinkFactory.listExtionsions()) {
			log.info("已定义扩展：" + provider.getClass().getName() + ".支持["
					+ provider.getKey() + "://*] scheme URI");
		}
	}

	/**
	 * <ul>
	 * 主要的测试步骤包括：
	 * <li>测试统一获取外部资源接口，直接获取外部资源，是否可以正确获取
	 * <li>建立默认连接，是否建立成功。建立成功后，是否可以正确获取，获取内容是否正确
	 * <li>建立映射连接，是否建立成功。建立成功后，是否可以正确获取，获取内容是否正确
	 * <li>建立硬连接，是否建立成功。建立成功后，是否可以正确获取，获取内容是否正确
	 * </ul>
	 * 
	 * @param uri
	 * @param destBucket
	 * @param destKey
	 * @throws OSSException
	 * @throws ClientException
	 * @throws IOException
	 */
	protected void linkTest(String uri, String destBucket, String destKey)
			throws Exception {
		OssClientX clientX = new OssClientX(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);

		// 测试getObject(uri)功能，统一获取外部资源接口
		OSSObject originObject = clientX.getObject(uri);
		assertNotNull(originObject);
		byte[] orginContent = IOUtils.toByteArray(originObject
				.getObjectContent());
		assertNotNull(orginContent);
		assertTrue(orginContent.length > 0);

		// 测试默认连接方式，默认连接方式由LinkSourceProvider确定是否采用硬连接
		{
			clientX.link(uri, new OssLinkLocator(destBucket, destKey));
			OSSObject object = clientX.getObject(destBucket, destKey);
			assertNotNull(object);
			byte[] content = IOUtils.toByteArray(object.getObjectContent());
			assertNotNull(content);
			// 比较获取的对象是否相同
			assertTrue(Arrays.equals(content, orginContent));
		}

		// 测试映射连接方式
		{
			clientX.link(uri, new OssLinkLocator(destBucket, destKey), false);
			OSSObject object = clientX.getObject(destBucket, destKey);
			assertNotNull(object);
			byte[] content = IOUtils.toByteArray(object.getObjectContent());
			assertNotNull(content);
			// 比较获取的对象是否相同
			assertTrue(Arrays.equals(content, orginContent));
		}

		// 测试硬连接方式
		{
			clientX.link(uri, new OssLinkLocator(destBucket, destKey), true);
			OSSObject object = clientX.getObject(destBucket, destKey);
			assertNotNull(object);
			byte[] content = IOUtils.toByteArray(object.getObjectContent());
			assertNotNull(content);
			// 比较获取的对象是否相同
			assertTrue(Arrays.equals(content, orginContent));
		}
	}
}
