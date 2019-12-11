package com.taotao.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
/**
 * 商品搜索Dao
 * <p>Title: SearchDao</p>
 * <p>Description: </p>
 * @version 1.0
 */
@Repository
public class SearchDao {
	
	@Autowired
	private SolrServer solrServer;
	
	public SearchResult search(SolrQuery query) throws Exception{
		//根据query对象查询索引库
		QueryResponse response = solrServer.query(query);
		//获取商品列表
		SolrDocumentList solrDocumentList = response.getResults();
		//创建商品列表对象
		List<SearchItem> items = new ArrayList<>();
		//高亮显示对象
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		//遍历处理商品
		for (SolrDocument solrDocument : solrDocumentList) {
			SearchItem item = new SearchItem();
			long itemId = Long.parseLong(solrDocument.get("id").toString());
			item.setId(itemId);
			item.setCategory_name((String) solrDocument.get("item_category_name"));
			item.setImage((String) solrDocument.get("item_image"));
			item.setPrice((Long) solrDocument.get("item_price"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			//处理高亮结果
			setItemTitle(highlighting,solrDocument,itemId,item);
			//添加到商品列表
			items.add(item);
		}
		SearchResult result = new SearchResult();
		//商品列表
		result.setItemList(items);
		//总记录数
		result.setRecordCount(solrDocumentList.getNumFound());
		return result;
	}
	
	/**
	 * 设置商品标题，如果名称有高亮结果，那么读取。否则，直接从item_title域获取
	 * <p>Title: setItemTitle</p>
	 * <p>Description: </p>
	 * @param highlighting
	 * @param solrDocument
	 * @param itemId
	 * @param item
	 */
	private void setItemTitle(Map<String, Map<String, List<String>>> highlighting, SolrDocument solrDocument,
			Long itemId, SearchItem item) {
		//定义第三方变量
		boolean flag = false;
		if (highlighting != null) {
			Map<String, List<String>> itemHting = highlighting.get(itemId+"");
			if (itemHting != null && itemHting.size() > 0) {
				List<String> itemTitle = itemHting.get("item_title");
				if (itemTitle != null && itemTitle.size() > 0) {
					item.setTitle(itemTitle.get(0));
					flag = true;
				}
			}
		}
		if (!flag) {
			item.setTitle((String) solrDocument.get("item_title"));
		}
	}
}
