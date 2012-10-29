package ali2012.link.spi;

/**
 * LinkSource扩展程序发生的异常统一使用LinkSourceException封装
 * 
 * @author wangs [ops2000@gmail.com]
 *
 */
public class LinkSourceException extends Exception {
	public LinkSourceException() {
		super();
	}

	public LinkSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public LinkSourceException(String message) {
		super(message);
	}

	public LinkSourceException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -30351720240025369L;

}
