package com.campus.carbon.model.dto;

import java.util.ArrayList;
import java.util.List;

public class AgentBriefVO {
    private AgentSummaryVO summary;
    private List<AgentActionVO> actions = new ArrayList<>();
    private List<String> evidence = new ArrayList<>();

    public AgentSummaryVO getSummary() {
        return summary;
    }

    public void setSummary(AgentSummaryVO summary) {
        this.summary = summary;
    }

    public List<AgentActionVO> getActions() {
        return actions;
    }

    public void setActions(List<AgentActionVO> actions) {
        this.actions = actions;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }
}
