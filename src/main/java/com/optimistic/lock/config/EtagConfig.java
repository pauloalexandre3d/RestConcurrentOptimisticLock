package com.optimistic.lock.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@EnableAutoConfiguration
public class EtagConfig {

	@Bean
	public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
	    return new ShallowEtagHeaderFilter();
	}
	
//	@Bean
//	public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
//	    FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean
//	      = new FilterRegistrationBean<>( new ShallowEtagHeaderFilter());
//	    filterRegistrationBean.addUrlPatterns("/foos/*");
//	    filterRegistrationBean.setName("etagFilter");
//	    return filterRegistrationBean;
//	}
	
}
