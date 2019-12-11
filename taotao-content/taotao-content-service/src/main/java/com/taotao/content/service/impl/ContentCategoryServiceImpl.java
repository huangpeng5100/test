package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
/**
 * 内容分类的Serivce
 * <p>Title: ContentCategoryServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//取查询参数id，parentId
		//根据parentId查询tb_content_category,查询子节点
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		//得到List<TbContentCategory>对象
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		//把列表转成List<EasyUITreeNode>对象
		List<EasyUITreeNode> resultList = new ArrayList<EasyUITreeNode>();
		for (TbContentCategory contentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(contentCategory.getId());
			node.setText(contentCategory.getName());
			node.setState(contentCategory.getIsParent()?"closed":"open");
			//添加到列表中
			resultList.add(node);
		}
		//返回结果集
		return resultList;
	}

	@Override
	//添加节点
	public TaotaoResult addContentCategory(long parentId, String name) {
		//接受两个参数，parentId，name
		//创建一个TbContentCategory对象
		TbContentCategory contentCategory = new TbContentCategory();
		//补全TbContentCategory属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//排列序号
		contentCategory.setSortOrder(1);
		//新添加的节点一定不是父节点
		contentCategory.setIsParent(false);
		//状态,1.正常，2.删除
		contentCategory.setStatus(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//向tb_content_category表中插入数据
		contentCategoryMapper.insert(contentCategory);
		//判断父节点的isparent是否为true，不是true改为true
		TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parentNode.getIsParent()) {
			parentNode.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parentNode);
		}
		//需要主键返回
		//返回TaotaoResult，把TbContentCategory对象封装
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	//重命名节点
	public TaotaoResult updateContentCategory(long id, String name) {
		//以id来获取TbContentCategory对象
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		contentCategory.setName(name);
		//向tb_content_category表中插入数据,更新数据库
		contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
		//返回结果集
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	//删除节点
	public TaotaoResult deleteContentCategory(long parentId, long id) {
		//获取当前节点的叶子节点
		TbContentCategory cat = contentCategoryMapper.selectByPrimaryKey(id);
		//获取父节点
		TbContentCategory catPatent = contentCategoryMapper.selectByPrimaryKey(parentId);
		//判断当前节点是叶子节点直接删除，是父节点则递归删除当前节点和其子节点。再删除自己
		if (!cat.getIsParent()) {
			//不是父节点,直接删除
			contentCategoryMapper.deleteByPrimaryKey(id);
		}else {
			//父节点，查询子节点，递归删除
			List<TbContentCategory> list = getListById(id);
			//查询子节点
			delCat(cat,list);
		}
		//根据父节点的id查询是否还有父节点
		List<TbContentCategory> list = getListById(parentId);
		if (list == null || list.size() == 0) {
			//更该父节点为子节点
			catPatent.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKeySelective(catPatent);
		}
		return TaotaoResult.ok();
	}
	
	//递归删除
	private void delCat(TbContentCategory cat, List<TbContentCategory> list) {
		for (TbContentCategory category : list) {
			if (category.getIsParent()) {
				//是父节点,查询子节点，递归调用
				List<TbContentCategory> listById = getListById(category.getId());
				delCat(category,listById);
			}else {
				//子节点，直接删除
				contentCategoryMapper.deleteByPrimaryKey(category.getId());
			}
		}
		//最终删除自己
		contentCategoryMapper.deleteByPrimaryKey(cat.getId());
	}

	//根据id查询子节点列表
	private List<TbContentCategory> getListById(long id) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		//执行查询
		return contentCategoryMapper.selectByExample(example);
	}
}
