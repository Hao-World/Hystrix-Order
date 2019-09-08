
package com.hao.hystrix;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.hao.service.MemberService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

@SuppressWarnings("rawtypes")
public class OrderHystrixCommand extends HystrixCommand<JSONObject> {
	@Autowired
	private MemberService memberService;

	/**
	 * @param group
	 */
	public OrderHystrixCommand(MemberService memberService) {
		super(setter());
		this.memberService = memberService;
	}

	protected JSONObject run() throws Exception {
		JSONObject member = memberService.getMember();
		System.out.println("当前线程名称:" + Thread.currentThread().getName() + ",Hystrix线程池方式，订单服务调用会员服务:member:" + member);
		return member;
	}

	private static Setter setter() {

		// 服务分组
		HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("orders");
		// 服务标识
		HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("order");
		// 线程池名称
		HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("order-pool");
		// #####################################################
		// 线程池配置 线程池大小为10,线程存活时间15秒 队列等待的阈值为100,超过100执行拒绝策略
		HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties.Setter().withCoreSize(10)
				.withKeepAliveTimeMinutes(15).withQueueSizeRejectionThreshold(100);
		// ########################################################
		// 命令属性配置Hystrix 开启超时
		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
				// 采用线程池方式实现服务隔离
				.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
				// 禁止超时时间
				.withExecutionTimeoutEnabled(false);
		return HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
				.andThreadPoolPropertiesDefaults(threadPoolProperties).andCommandPropertiesDefaults(commandProperties);

	}

	/**
	 * 服务降级
	 */
	@Override
	protected JSONObject getFallback() {
		// 如果Hystrix发生熔断，当前服务不可用,直接执行Fallback方法
		
		JSONObject result = new JSONObject();
		result.put("code", 500);
		result.put("msg", "系统错误！(服务降级演示)");
		System.out.println("系统错误！"+result);
		return result;
	}
}
