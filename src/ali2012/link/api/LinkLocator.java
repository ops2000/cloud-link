package ali2012.link.api;

import java.net.URI;

import ali2012.link.api.impl.OssLinkLocator;

/**
 * 资源定位器。这是一个难以理解的对象。主要提供两类职责功能：
 * 
 * <ul>
 * <li>云存储对象落地的位置、方式描述
 * <li>提供各类URI、String[]和实体对象的转换
 * </ul>
 * 
 * 其实，这两项功能之间存在这矛盾。描述对象落地，这是一个很明确清晰的功能，但这样就不应该为其他的外部资源提供相关的{@link LinkLocator} 。
 * 
 * 但实际上，出于第二个职责功能的实现，对于外部资源往往也提供对应的{@link LinkLocator}，如{@link OssLinkLocator}
 * 也为<code>snda://</code>提供服务。
 * 
 * 
 * 这是一个抽象类，提供了主要的功能实现。
 * 
 * @author wangs [ops2000@gmail.com]
 * 
 * @param <T> 包含落地信息的对象，不是必须的
 */
public abstract class LinkLocator<T> {
	protected Object orginPath;

	protected T path;

	/**
	 * 是否默认物理连接方式
	 * 
	 * @return
	 */
	private boolean physicsDefault;

	/**
	 * 根据URI生成对象
	 * 
	 * @param path
	 */
	protected abstract T parseUri(URI path);

	/**
	 * 根据字符串数组生成对象
	 * 
	 * @param path
	 * @return
	 */
	protected abstract T parseStringArray(String[] path);

	/**
	 * 获取对象的URI
	 * 
	 * @return
	 */
	public abstract URI getPathUri();

	/**
	 * 使用URI生成LinkLocator
	 * 
	 * @param path
	 */
	public LinkLocator(URI path) {
		this.orginPath = path;
		this.path = setup(orginPath);
	}

	/**
	 * 使用多个顺序的path片段生成LinkLocator。实现类需要实现各自的解析算法
	 * 
	 * @param path
	 */
	public LinkLocator(String... path) {
		this.orginPath = path;
		this.path = setup(orginPath);
	}

	/**
	 * 根据URI或String[]建立对象
	 * 
	 * @param orginPath
	 */
	protected T setup(Object orginPath) {
		if (this.orginPath instanceof URI) {
			return parseUri((URI) this.orginPath);
		} else if (this.orginPath instanceof String[]) {
			return parseStringArray((String[]) this.orginPath);
		}
		return null;
	}

	/**
	 * 获取对象
	 * 
	 * @return
	 */
	public T getPath() {
		return path;
	}

	/**
	 * 定位器的默认定位方式
	 * @return
	 */
	public boolean isPhysicsDefault() {
		return physicsDefault;
	}

	/**
	 * 设置默认的联接方式
	 * 
	 * @return
	 */
	public void setPhysicsDefault(boolean physicsDefault) {
		this.physicsDefault = physicsDefault;
	}
}
