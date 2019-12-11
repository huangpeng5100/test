package com.taotao.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class TestActivemq extends TestCase {

	// 生产者
	public void testQueueProducer() throws Exception {
		// 1.创建ConnectionFactory对象，指定其ip及端口
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.133:61616");
		// 2.使用ConnectionFactory对象创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接，使用connection调用start方法
		connection.start();
		// 4.使用connection对象创建Session对象,参数一：是否开启事务，参数二：当参数1为false时，设置手动还是自动
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建一个Destination对象(topic/queue)
		Queue queue = session.createQueue("test-queue");
		// 6.使用Session对象创建Producer对象
		MessageProducer producer = session.createProducer(queue);
		// 7.创建一个TestMessage对象
		TextMessage message = session.createTextMessage("我要大白腿");
		// 8.使用producer发送消息
		producer.send(message);
		// 9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}

	// 消费者
	public void testConsumer() throws Exception {
		// 1.创建一个ConnectionFactory对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.133:61616");
		// 2.使用ConnectionFactory创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接，使用connection调用start方法
		connection.start();
		// 4.使用connection对象创建一个Session对象，参数一：是否开启事务，参数二：参数1为false，设置自动还是手动应答。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建一个Destination对象,和发送端保持一致queue及队列名称一致
		Queue queue = session.createQueue("test-queue");
		// 6.使用Session对象创建一个Consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		// 7.接受消息
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				// 打印信息
				try {
					TextMessage textMessage = (TextMessage) message;
					String text = null;
					// 取消息的内容
					text = textMessage.getText();
					// 打印消息
					System.out.println(text);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 等待键盘输入
//		System.in.read();
		// 9.关闭资源
		consumer.close();
		connection.close();
		session.close();
	}

	// 生产者
	public void testTopicProducer() throws Exception {
		// 1.创建一个ConnectionFactory对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.133:61616");
		// 2.使用ConnectionFactory对象创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接
		connection.start();
		// 4.使用Connerction对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建Destination对象
		Topic topic = session.createTopic("test-topic");
		// 6.使用Session创建Producer对象
		MessageProducer producer = session.createProducer(topic);
		// 7.创建一个TestMessage对象
		TextMessage message = session.createTextMessage("加油，好样的！要相信自己，我能行！");
		// 8.使用Producer对象发送消息
		producer.send(message);
		// 9.关闭资源
		producer.close();
		connection.close();
		session.close();
	}

	// 消费者
	public void testTopicConsumer() throws Exception {
		// 1.创建一个ConnectionFactory对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.133:61616");
		// 2.使用ConnectionFactory对象创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接
		connection.start();
		// 4.使用Connection对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建Destination对象 Topic
		Topic topic = session.createTopic("test-topic");
		// 6.使用Sesion对象创建一个Consumer对象
		MessageConsumer consumer = session.createConsumer(topic);
		// 7.接受消息
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				// 打印信息
				try {
					TextMessage textMessage = (TextMessage) message;
					String text = null;
					text = textMessage.getText();
					// 8.打印消息
					System.out.println(text);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 等待键盘输入
//		System.in.read();
		// 9.关闭资源
		connection.close();
		consumer.close();
		session.close();
	}

	// spirng整合
	public void testQueueConsumer() throws Exception {
		// 初始化spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-activemq.xml");
		// 等待
//		System.in.read();
	}
}
