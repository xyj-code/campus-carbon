package com.campus.carbon.controller;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AiService aiService;

    @PostMapping("/carbon-suggest")
    public AiSuggest getCarbonSuggestion(@RequestBody AiSuggest aiSuggest) {
        return aiService.getCarbonSuggestion(aiSuggest.getCarbonFootprint());
    }
}