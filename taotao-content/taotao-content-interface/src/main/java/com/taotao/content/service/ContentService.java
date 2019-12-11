package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {
	TaotaoResult addContent(TbContent content);

	TaotaoResult deleteContent(String ids);

	EasyUIDataGridResult queryContentList(long categoryId, int page, int rows);
	
	List<TbContent> getContentList(long cid);
}
