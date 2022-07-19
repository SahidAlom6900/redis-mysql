package com.technoelevate.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private CacheManager cacheManager;

	@Override
	public final void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("On Application Event is OK");
		cacheManager.getCacheNames().parallelStream().forEach(n -> {
			log.info(n);
			cacheManager.getCache(n).clear();
		});
	}

}