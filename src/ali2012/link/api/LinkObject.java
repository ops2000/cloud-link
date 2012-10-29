package ali2012.link.api;

import java.io.InputStream;
import java.net.URI;

/**
 * 外部联接资源的抽象模型。
 * 
 * 包括唯一标识，联接方式，内容，元信息。
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class LinkObject {
	/**
	 * 唯一的标识
	 */
	private URI id;

	/**
	 * 是否物理连接方式
	 */
	private boolean physics;

	/**
	 * 内容
	 */
	private InputStream content;
	
	/**
	 * 描述数据
	 */
	private LinkObjectMetadata metadata;
	
	public LinkObject() {

	}

	public LinkObject(URI id, InputStream content, LinkObjectMetadata metadata) {
		super();
		this.id = id;
		this.content = content;
		this.metadata = metadata;
	}
	
	public LinkObject(URI id, InputStream content, LinkObjectMetadata metadata,
			boolean physics) {
		super();
		this.id = id;
		this.content = content;
		this.metadata = metadata;
		this.physics = physics;
	}

	public LinkObject(String uri) {
		this.id = generateId(uri);
	}
	
	public LinkObject(URI uri) {
		this.id = uri;
	}

	public URI generateId(String uriString) {
		return URI.create(uriString);
	}

	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	public boolean isPhysics() {
		return physics;
	}

	public void setPhysics(boolean physics) {
		this.physics = physics;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}

	public LinkObjectMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(LinkObjectMetadata metadata) {
		this.metadata = metadata;
	}
}
