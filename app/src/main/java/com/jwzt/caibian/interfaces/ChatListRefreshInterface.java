package com.jwzt.caibian.interfaces;

import com.jwzt.caibian.bean.ChatMessageBean;

/**
 * 收到消息后聊天列表刷新接口回调
 * @author pc
 *
 */
public interface ChatListRefreshInterface {
	/**
	 * 刷新列表
	 */
	public void setRefresh(ChatMessageBean chatMessageBean);
}
