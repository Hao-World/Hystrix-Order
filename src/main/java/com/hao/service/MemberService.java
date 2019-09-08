package com.hao.service;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hao.utils.HttpClientUtils;
@Service
public class MemberService {

	public JSONObject getMember() {

		JSONObject result = HttpClientUtils.httpGet("http://127.0.0.1:8081/member/memberIndex");
		return result;
	}

}
