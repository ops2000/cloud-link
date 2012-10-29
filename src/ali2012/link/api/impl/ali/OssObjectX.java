package ali2012.link.api.impl.ali;

import com.aliyun.openservices.oss.model.OSSObject;

/**
 * 内部使用，开发者不需要直接调用，使用原来的OSSObject即可。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class OssObjectX extends OSSObject {
	public OssObjectX(String bucketName, String key) {
		this();
		this.setBucketName(bucketName);
		this.setKey(key);
	}
	
	public OssObjectX() {
		super();
	}

}
