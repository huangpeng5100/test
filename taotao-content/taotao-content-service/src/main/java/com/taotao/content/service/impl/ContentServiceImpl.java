package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
/**
 * 内容管理Service
 * <p>Title: ContentServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ContentServiceImpl implements ContentService {
	private static final Logger logger = LoggerFactory.getLogger(Logger.class);
	
	@Autowired
	private TbContentMapper contentMappr;
	@Autowired
	private JedisClient jedisClient;
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	
	@Override
	//添加内容
	public TaotaoResult addContent(TbContent content) {
		//补全属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//插入数据
		contentMappr.insert(content);
		//缓存同步,清除redis中cid对应的缓存信息
		jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());
		return TaotaoResult.ok();
	}

	@Override
	//删除内容
	public TaotaoResult deleteContent(String ids) {
		String[] strings = ids.split(",");
		TaotaoResult result = new TaotaoResult();
		try {
			for (String string : strings) {
				//先查询再删除
				contentMappr.deleteByPrimaryKey(Long.parseLong(string));
			} 
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(400);
			result.setMsg("删除内容失败，实现层");
		}
		return result;
	}

	@Override
	//内容列表查询，分页处理
	public EasyUIDataGridResult queryContentList(long categoryId, int page, int rows) {
		TbContentExample example = new TbContentExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//执行查询条件
		List<TbContent> list = contentMappr.selectByExample(example);
		PageHelper.startPage(page, rows);
		//获取分页信息
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		//封装分页数据
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	//前台轮播图的展示
	public List<TbContent> getContentList(long cid) {
		//先查询缓存
		try {
			String json = jedisClient.hget(CONTENT_KEY, cid+"");
			//判断是否存在缓存
			if (StringUtils.isNotBlank(json)) {
				//转成list
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("-----大广告缓存出错-----");
		}
		//根据cid来查询内容列表
		TbContentExample example = new TbContentExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		//执行查询
		List<TbContent> list = contentMappr.selectByExample(example);
		//向缓存中保存结果
		try {
			jedisClient.hset(CONTENT_KEY, cid+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("-----大广告缓存出错-----");
		}
		return list;
	}
}
