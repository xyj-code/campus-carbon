package com.campus.carbon.service;

import com.campus.carbon.model.AiSuggest;

public interface AiService {
    AiSuggest getCarbonSuggestion(String carbonFootprint);
}