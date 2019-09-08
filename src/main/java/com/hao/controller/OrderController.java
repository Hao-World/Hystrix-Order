package com.hao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.hao.hystrix.OrderHystrixCommand;
import com.hao.hystrix.OrderHystrixCommand2;
import com.hao.service.MemberService;


@RestController
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private MemberService memberService;

	/**
	 * 订单服务会调用会员服务，底层使用rpc远程调用的方式
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping("/orderIndex")
	public Object orderIndex() throws InterruptedException {
		JSONObject member = memberService.getMember();
		//线程池名称+线程池id组合
		System.out.println("当前线程名称:" + Thread.currentThread().getName() + ",订单服务调用会员服务:member:" + member);
		return member;
	}

	// 订单服务调用会员服务，解决服务雪崩效应，采用线程池的方式
	@RequestMapping("/orderIndexHystrix")
	public Object orderIndexHystrix() throws InterruptedException {
		return new OrderHystrixCommand(memberService).execute();
	}
	// 订单服务调用会员服务，解决服务雪崩效应，采用信号量的方式
	@RequestMapping("/orderIndexHystrix2")
	public Object orderIndexHystrix2() throws InterruptedException {
		return new OrderHystrixCommand2(memberService).execute();
	}

	@RequestMapping("/findOrderIndex")
	public Object findIndex() {
		System.out.println("当前线程:" + Thread.currentThread().getName() + ",findOrderIndex");
		return "findOrderIndex";
	}
}
