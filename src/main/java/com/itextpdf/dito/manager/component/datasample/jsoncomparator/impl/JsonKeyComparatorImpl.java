package com.itextpdf.dito.manager.component.datasample.jsoncomparator.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.datasample.jsoncomparator.JsonKeyComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class JsonKeyComparatorImpl implements JsonKeyComparator {
	
	private static final Logger log = LogManager.getLogger(JsonKeyComparatorImpl.class);
	private ObjectMapper mapper;
	
	public JsonKeyComparatorImpl(final ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public boolean checkJsonKeysEquals(final String json1, final String json2) {
		boolean result;
		try {
			result = checkLastDataCollectionEqualsSample(json1, json2);
		} catch (JsonProcessingException  e) {
			log.info("Checking datasample to datacollection structure consistency failed. Error message: {}", e.getMessage());
			result = false;
		}
		return result;
	}

    private boolean checkLastDataCollectionEqualsSample(final String json1, final String json2) throws JsonProcessingException {
        final Set<String> keys1 = getJsonKeys(json1);
        final Set<String> keys2 =  getJsonKeys(json2);
        final boolean sizeCheck = keys1.size() == keys2.size();
        keys1.removeAll(keys2);
        return sizeCheck && keys1.isEmpty();
    }

    private Set<String> getJsonKeys(final String json) throws JsonProcessingException {
    	return mapper.readValue(json, new TypeReference<HashMap<String,Object>>() {}).keySet();
    }
}
