package com._42195km.msa.user.infrastructure.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("aws")
@Slf4j
public class AwsEcsConfig {
	@Bean
	public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
		EurekaInstanceConfigBean eurekaInstanceConfigBean = new EurekaInstanceConfigBean(inetUtils);
		String ip = null;

		try {
			ip = InetAddress.getLocalHost().getHostAddress();
			log.info("ECS Task Container Private Ip Address = {}", ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		eurekaInstanceConfigBean.setIpAddress(ip);
		eurekaInstanceConfigBean.setPreferIpAddress(true);

		return eurekaInstanceConfigBean;
	}
}
