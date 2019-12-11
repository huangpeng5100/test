package taotao.search.service;

import com.taotao.common.pojo.TaotaoResult;

public interface SearchItemService {
	TaotaoResult importAllItems() throws Exception;

	TaotaoResult addDocument(long itemId) throws Exception;
}
