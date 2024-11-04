package com.nexters.bottles.app.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfig {
    @Value("\${spring.redis.cluster.nodes}")
    private lateinit var clusterNodesString: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val clusterConfiguration = RedisClusterConfiguration()
        val clusterNodes = clusterNodesString.split(",")
        clusterNodes.forEach { node ->
            val parts = node.trim().split(":")
            clusterConfiguration.clusterNode(parts[0], parts[1].toInt())
        }
        return LettuceConnectionFactory(clusterConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(redisConnectionFactory())
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            StringRedisSerializer().also { hashKeySerializer = it }
            GenericJackson2JsonRedisSerializer().also { hashValueSerializer = it }
        }
    }
}
