package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;

public interface UserService {
	TaotaoResult checkUserInfo(String param, int type);
}
