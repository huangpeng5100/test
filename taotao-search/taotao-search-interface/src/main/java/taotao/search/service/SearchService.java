package taotao.search.service;
/**
 * 商品搜索服务
 * <p>Title: SearchService</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */

import com.taotao.common.pojo.SearchResult;

public interface SearchService {
	SearchResult search(String queryString, int page, int rows) throws Exception;
}
