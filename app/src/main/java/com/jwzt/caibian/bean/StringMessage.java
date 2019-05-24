package com.jwzt.caibian.bean;
/**
 * 用于通过EventBus进行字符串类型的传递
 * @author howie
 *
 */
public class StringMessage {
	private String message;

	public StringMessage(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	
	
}
