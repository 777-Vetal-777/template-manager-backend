package com.itextpdf.dito.manager.component.datasample.jsoncomparator.impl;

import com.itextpdf.dito.manager.component.datasample.jsoncomparator.JsonKeyComparator;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JsonKeyComparatorImpl implements JsonKeyComparator {

	@Override
	public boolean checkJsonKeysEquals(final String json1, final String json2) {
		boolean result;
		try {
			result = checkLastDataCollectionEqualsSample(json1, json2);
		} catch (JSONException e) {
			result = false;
		}
		return result;
	}

    private boolean checkLastDataCollectionEqualsSample(final String json1, final String json2) {
        final List<String> keys1 = Arrays.asList(getJsonKeys(json1));
        final List<String> keys2 = Arrays.asList(getJsonKeys(json2));
        return (keys2.containsAll(keys1) && keys1.containsAll(keys2));
    }

    private String[] getJsonKeys(final String json) {
        final JSONObject object = new JSONObject(json);
        return JSONObject.getNames(object);
    }
}
