package com.technoelevate.redis.config;

import javax.annotation.PostConstruct;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Configuration
@Slf4j
public class RedisTemplateBean {

	@Value("${spring.redis.jedis.pool.max-active: 16}")
	private Integer maxActive;

	@Value("${spring.redis.jedis.pool.max-idle: 8}")
	private Integer maxIdle;

	@Value("${spring.redis.jedis.pool.min-idle: 4}")
	private Integer minIdle;

	@Value("${spring.redis.host: localhost}")
	private String host;

	@Value("${spring.redis.port: 6379}")
	private Integer port;

	@Bean
	public JedisConnectionFactory connectionFactory(JedisClientConfiguration jedisClientConfiguration) {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(host);
		configuration.setPort(port);
		return new JedisConnectionFactory(configuration, jedisClientConfiguration);
	}

	@Bean
	public JedisClientConfiguration getJedisClientConfiguration() {
		JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisPoolingClientConfigurationBuilder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration
				.builder();
		GenericObjectPoolConfig<Object> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
		genericObjectPoolConfig.setMaxTotal(maxActive);
		genericObjectPoolConfig.setMaxIdle(maxIdle);
		genericObjectPoolConfig.setMinIdle(minIdle);
		return jedisPoolingClientConfigurationBuilder.poolConfig(genericObjectPoolConfig).build();
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setConnectionFactory(jedisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}

	@PostConstruct
	public void clearCache() {
		log.info("In Clear Cache");
		Jedis jedis = new Jedis(host, port, 1000);
		jedis.flushAll();
		jedis.close();
	}

}