package com.taotao.content.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class Jedis {
	
	@Test
	public void testJedis(){
		//创建JedisPool对象，指定端口跟ip
		JedisPool jedisPool = new JedisPool("192.168.25.133", 6379);
		//从JedisPool对象中获得Jedis对象
		redis.clients.jedis.Jedis jedis = jedisPool.getResource();
		//使用jedis操作redis服务器
		jedis.set("jedis", "test");
		String result = jedis.get("jedis");
		System.out.println(result);
		//关闭jedis对象，关闭资源
		jedis.close();
		jedisPool.close();
	}
	
	//连接集群版
	@Test
	public void testJedisCluster(){
		//使用JedisCluster对象，它需要一个HostANdPort参数，redis节点列表
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.25.133", 7001));
		nodes.add(new HostAndPort("192.168.25.133", 7002));
		nodes.add(new HostAndPort("192.168.25.133", 7003));
		nodes.add(new HostAndPort("192.168.25.133", 7004));
		nodes.add(new HostAndPort("192.168.25.133", 7005));
		nodes.add(new HostAndPort("192.168.25.133", 7006));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		//使用JedisCluster对象操作redis，它在系统是单例的
		String result = jedisCluster.set("hello", "100");
		//打印
		System.out.println(result);
		//关闭资源
		jedisCluster.close();
	}
	
	@Test
	public void testJedisClient(){
		//初始化spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//从容器获取JedisClient对象
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		jedisClient.set("first", "自由");
		String result = jedisClient.get("first");
		//打印结果
		System.out.println(result);
	}
}
