package com.jwzt.caibian.interfaces;
/**
 * 素材管理列表数据清空之后的接口回调，用于控制“暂无素材”的显示
 * @author howie
 *
 */
public interface EmptyListener {
	/**
	 * 数据清空的回调，用于控制“暂无素材”的显示
	 */
	void empty();
}
