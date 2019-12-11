package com.taotao.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.search.service.impl.SearchItemServiceImpl;

public class ItemChangeListener implements MessageListener{
	
	@Autowired
	private SearchItemServiceImpl searchItemServiceImpl;
	
	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = null;
			Long itemId = null;
			if (message instanceof TextMessage) {
				textMessage = (TextMessage) message;
				itemId = Long.parseLong(textMessage.getText());
				//向索引库中添加文档
				searchItemServiceImpl.addDocument(itemId);
			}else {
				System.out.println("类型转换错误！！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
