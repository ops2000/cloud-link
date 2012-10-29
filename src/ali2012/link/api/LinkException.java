package ali2012.link.api;

/**
 * <p>Link过程中发生的各种异常，提供对其他异常的封装。
 * 
 * <strong>尚未详细实现</strong>
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class LinkException extends Exception {
	public LinkException() {
		super();
	}

	public LinkException(String message, Throwable cause) {
		super(message, cause);
	}

	public LinkException(String message) {
		super(message);
	}

	public LinkException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -30351720240025369L;

}
