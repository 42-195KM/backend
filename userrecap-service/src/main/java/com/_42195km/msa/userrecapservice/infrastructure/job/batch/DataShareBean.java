package com._42195km.msa.userrecapservice.infrastructure.job.batch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataShareBean<T> {

	private final Map<String, T> shareDataMap;

	public DataShareBean() {
		this.shareDataMap = new ConcurrentHashMap<>();
	}

	public void putData(String key, T data) {
		shareDataMap.put(key, data);
	}

	public T getData(String key) {
		return shareDataMap.get(key);
	}

	public int getSize() {
		return shareDataMap.size();
	}

}
