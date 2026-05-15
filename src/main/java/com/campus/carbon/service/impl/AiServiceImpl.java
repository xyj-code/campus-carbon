package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.AgentActionLogMapper;
import com.campus.carbon.mapper.AgentMemoryMapper;
import com.campus.carbon.mapper.AgentSessionMapper;
import com.campus.carbon.model.AgentActionLog;
import com.campus.carbon.model.AgentMemory;
import com.campus.carbon.model.AgentSession;
import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.model.dto.AgentActionRequest;
import com.campus.carbon.model.dto.AgentActionVO;
import com.campus.carbon.model.dto.AgentBriefVO;
import com.campus.carbon.model.dto.AgentScheduleVO;
import com.campus.carbon.model.dto.AgentSportPlanVO;
import com.campus.carbon.model.dto.AgentSummaryVO;
import com.campus.carbon.model.dto.AgentWeatherVO;
import com.campus.carbon.model.dto.TaskBoardItemVO;
import com.campus.carbon.service.AiService;
import com.campus.carbon.service.HealthDataService;
import com.campus.carbon.service.MapVenueService;
import com.campus.carbon.service.StudentScheduleService;
import com.campus.carbon.service.TaskService;
import com.campus.carbon.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Date;

@Service
public class AiServiceImpl implements AiService {

    private static final String MODEL_NAME = "qwen-turbo";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    private static final String CARBON_SYSTEM_PROMPT =
            "\u4f60\u662f\u4e00\u4e2a\u6821\u56ed\u4f4e\u78b3\u751f\u6d3b\u987e\u95ee\u3002" +
            "\u8bf7\u6839\u636e\u7528\u6237\u63cf\u8ff0\u7ed9\u51fa\u6e05\u6670\u3001\u5177\u4f53\u3001\u53ef\u6267\u884c\u7684\u51cf\u78b3\u5efa\u8bae\u3002" +
            "\u4e25\u683c\u9075\u5b88\u4ee5\u4e0b\u8f93\u51fa\u8981\u6c42\uff1a" +
            "1.\u53ea\u8f93\u51fa\u7eaf\u6587\u672c\uff0c\u4e0d\u8981\u4f7f\u7528#\u3001*\u3001-\u3001Markdown\u6807\u9898\u3001\u52a0\u7c97\u6216\u5206\u5272\u7ebf\u3002" +
            "2.\u603b\u5171\u8f93\u51fa5\u884c\uff0c\u6bcf\u884c\u4e00\u6761\u3002" +
            "3.\u524d4\u884c\u5fc5\u987b\u5206\u522b\u4ee5\u201c\u884c\u52a8\u5efa\u8bae1\uff1a\u201d\u201c\u884c\u52a8\u5efa\u8bae2\uff1a\u201d\u201c\u884c\u52a8\u5efa\u8bae3\uff1a\u201d\u201c\u884c\u52a8\u5efa\u8bae4\uff1a\u201d\u5f00\u5934\u3002" +
            "4.\u7b2c5\u884c\u5fc5\u987b\u4ee5\u201c\u603b\u7ed3\uff1a\u201d\u5f00\u5934\u3002" +
            "5.\u8bed\u53e5\u7b80\u6d01\uff0c\u8d34\u5408\u5927\u5b66\u751f\u6821\u56ed\u573a\u666f\uff0c\u4e0d\u8981\u91cd\u590d\u7528\u6237\u539f\u8bdd\u3002";

    private static final String HEALTH_SYSTEM_PROMPT =
            "\u4f60\u662f\u4e00\u4e2a\u6821\u56ed\u5065\u5eb7\u751f\u6d3b\u987e\u95ee\uff0c\u540c\u65f6\u5173\u6ce8\u4f4e\u78b3\u751f\u6d3b\u65b9\u5f0f\u3002" +
            "\u8bf7\u6839\u636e\u7528\u6237\u5065\u5eb7\u6570\u636e\u7ed9\u51fa\u6bcf\u65e5\u5065\u5eb7\u5efa\u8bae\uff0c\u8981\u6c42\u6e05\u6670\u3001\u4fdd\u5b88\u3001\u53ef\u6267\u884c\uff0c\u4e0d\u505a\u75be\u75c5\u8bca\u65ad\u3002" +
            "\u4e25\u683c\u9075\u5b88\u4ee5\u4e0b\u8f93\u51fa\u8981\u6c42\uff1a" +
            "1.\u53ea\u8f93\u51fa\u7eaf\u6587\u672c\uff0c\u4e0d\u8981\u4f7f\u7528#\u3001*\u3001-\u3001Markdown\u6807\u9898\u3001\u52a0\u7c97\u6216\u5206\u5272\u7ebf\u3002" +
            "2.\u603b\u51714\u884c\uff0c\u6bcf\u884c\u4e00\u6761\u3002" +
            "3.\u56db\u884c\u5fc5\u987b\u5206\u522b\u4ee5\u201c\u996e\u98df\u5efa\u8bae\uff1a\u201d\u201c\u8fd0\u52a8\u5efa\u8bae\uff1a\u201d\u201c\u4f5c\u606f\u5efa\u8bae\uff1a\u201d\u201c\u751f\u6d3b\u5efa\u8bae\uff1a\u201d\u5f00\u5934\u3002" +
            "4.\u8fd0\u52a8\u5efa\u8bae\u4f18\u5148\u63a8\u8350\u6b65\u884c\u3001\u9a91\u884c\u3001\u62c9\u4f38\u7b49\u7eff\u8272\u65b9\u5f0f\u3002" +
            "5.\u6bcf\u884c1\u52302\u53e5\uff0c\u4e0d\u8981\u51fa\u73b0\u591a\u4f59\u524d\u7f00\u548c\u7f16\u53f7\u3002";

    private static final String CARBON_SERVICE_UNAVAILABLE =
            "\u0041\u0049\u670d\u52a1\u6682\u65f6\u4e0d\u53ef\u7528\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
    private static final String CARBON_SERVICE_ERROR =
            "\u83b7\u53d6\u0041\u0049\u5efa\u8bae\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc\u540e\u91cd\u8bd5\u3002";
    private static final String HEALTH_SERVICE_UNAVAILABLE =
            "\u0041\u0049\u5065\u5eb7\u5efa\u8bae\u670d\u52a1\u6682\u65f6\u4e0d\u53ef\u7528\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
    private static final String HEALTH_SERVICE_ERROR =
            "\u83b7\u53d6\u0041\u0049\u5065\u5eb7\u5efa\u8bae\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc\u540e\u91cd\u8bd5\u3002";
    private static final String AGENT_STATUS_PENDING = "pending";
    private static final String AGENT_STATUS_IN_PROGRESS = "in_progress";
    private static final String AGENT_STATUS_COMPLETED = "completed";
    private static final String AGENT_STATUS_SKIPPED = "skipped";
    private static final String AGENT_COMPLETION_VERIFIED = "verified";
    private static final String AGENT_COMPLETION_CLAIMED = "claimed";
    private static final String AGENT_COMPLETION_REJECTED = "rejected";
    private static final int MAX_AGENT_ACTIONS = 4;

    @Value("${carbon.ai.api-key}")
    private String apiKey;

    @Value("${carbon.ai.endpoint}")
    private String endpoint;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HealthDataService healthDataService;

    @Autowired
    private AgentSessionMapper agentSessionMapper;

    @Autowired
    private AgentActionLogMapper agentActionLogMapper;

    @Autowired
    private AgentMemoryMapper agentMemoryMapper;

    @Autowired
    private MapVenueService mapVenueService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private StudentScheduleService studentScheduleService;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AiSuggest getCarbonSuggestion(String carbonFootprint) {
        AiSuggest aiSuggest = new AiSuggest();
        aiSuggest.setCarbonFootprint(carbonFootprint);

        try {
            ArrayNode messages = createMessages(
                    CARBON_SYSTEM_PROMPT,
                    "\u6211\u7684\u4f4e\u78b3\u884c\u4e3a\u60c5\u51b5\uff1a" + safeText(carbonFootprint)
            );
            String content = requestSuggestion(messages, CARBON_SERVICE_UNAVAILABLE);
            aiSuggest.setSuggestion(normalizeSuggestion(content));
        } catch (IOException e) {
            e.printStackTrace();
            aiSuggest.setSuggestion(CARBON_SERVICE_ERROR);
        }

        return aiSuggest;
    }

    @Override
    public AiSuggest getHealthSuggestion(List<HealthData> healthDataList) {
        AiSuggest aiSuggest = new AiSuggest();

        try {
            String healthSummary = buildHealthSummary(healthDataList);
            ArrayNode messages = createMessages(
                    HEALTH_SYSTEM_PROMPT,
                    "\u8bf7\u6839\u636e\u4ee5\u4e0b\u5065\u5eb7\u4fe1\u606f\uff0c\u7ed9\u51fa\u4eca\u65e5\u5065\u5eb7\u5efa\u8bae\uff1a\n" + healthSummary
            );
            String content = requestSuggestion(messages, HEALTH_SERVICE_UNAVAILABLE);
            aiSuggest.setSuggestion(normalizeSuggestion(content));
            aiSuggest.setCarbonFootprint(healthSummary);
        } catch (IOException e) {
            e.printStackTrace();
            aiSuggest.setSuggestion(HEALTH_SERVICE_ERROR);
        }

        return aiSuggest;
    }

    @Override
    public AgentBriefVO getAgentBrief(String userId, String userNote) {
        return getAgentBrief(userId, userNote, null, null);
    }

    @Override
    public AgentBriefVO getAgentBrief(String userId, String userNote, Double latitude, Double longitude) {
        return buildAgentBrief(userId, userNote, latitude, longitude, true);
    }

    @Override
    public AgentBriefVO startAgentAction(AgentActionRequest request) {
        return changeAgentActionState(request, AGENT_STATUS_IN_PROGRESS);
    }

    @Override
    public AgentBriefVO completeAgentAction(AgentActionRequest request) {
        return changeAgentActionState(request, AGENT_STATUS_COMPLETED);
    }

    @Override
    public AgentBriefVO skipAgentAction(AgentActionRequest request) {
        return changeAgentActionState(request, AGENT_STATUS_SKIPPED);
    }

    private AgentBriefVO buildAgentBrief(String userId, String userNote, Double latitude, Double longitude, boolean allowRefresh) {
        AgentBriefVO brief = new AgentBriefVO();
        AgentWeatherVO weather = weatherService.getCurrentWeather(latitude, longitude);
        String normalizedUserId = safeText(userId);
        if (normalizedUserId.isEmpty()) {
            brief.setSummary(createEmptySummary());
            brief.getEvidence().add("\u8bf7\u5148\u767b\u5f55\u540e\u518d\u751f\u6210\u4eca\u65e5\u884c\u52a8\u8ba1\u5212");
            brief.setSessionStatus("idle");
            brief.setWeather(weather);
            brief.setSchedule(studentScheduleService.getTodayContext(normalizedUserId));
            brief.setSportPlan(mapVenueService.buildSportPlan(userNote, latitude, longitude));
            return brief;
        }

        String normalizedNote = safeText(userNote);
        AgentRuntimeSession session = getOrCreateSession(normalizedUserId, normalizedNote);
        if (allowRefresh && !normalizedNote.isEmpty() && !normalizedNote.equals(session.userNote)) {
            session.userNote = normalizedNote;
            session.actions = new ArrayList<>();
        }
        boolean rebuilt = false;
        if (allowRefresh && session.actions.isEmpty()) {
            rebuildSessionPlan(session, true, weather);
            rebuilt = true;
        }
        return createAgentBrief(session, !rebuilt, latitude, longitude, weather);
    }

    private AgentBriefVO changeAgentActionState(AgentActionRequest request, String targetStatus) {
        String userId = request == null ? "" : safeText(request.getUserId());
        Double latitude = request == null ? null : request.getLatitude();
        Double longitude = request == null ? null : request.getLongitude();
        if (userId.isEmpty()) {
            AgentBriefVO brief = new AgentBriefVO();
            brief.setSummary(createEmptySummary());
            brief.getEvidence().add("\u8bf7\u5148\u767b\u5f55\u540e\u518d\u6267\u884c Agent \u52a8\u4f5c");
            brief.setSessionStatus("idle");
            brief.setSportPlan(mapVenueService.buildSportPlan("", latitude, longitude));
            return brief;
        }

        AgentRuntimeSession session = loadLatestSession(userId);
        if (session == null) {
            return buildAgentBrief(userId, "", latitude, longitude, true);
        }

        if (!safeText(request.getSessionId()).isEmpty() && !Objects.equals(session.sessionId, safeText(request.getSessionId()))) {
            return createAgentBrief(session, false, latitude, longitude);
        }

        AgentActionVO action = findAction(session.actions, safeText(request.getActionId()));
        if (action == null) {
            return createAgentBrief(session, false, latitude, longitude);
        }

        String resultNote = safeText(request.getResultNote());
        if (AGENT_STATUS_IN_PROGRESS.equals(targetStatus)) {
            if (AGENT_STATUS_COMPLETED.equals(action.getStatus()) || AGENT_STATUS_SKIPPED.equals(action.getStatus())) {
                return createAgentBrief(session, false, latitude, longitude);
            }
            clearInProgressState(session.actions, action.getId());
            action.setStatus(AGENT_STATUS_IN_PROGRESS);
            action.setResultNote("\u5df2\u8fdb\u5165\u6267\u884c\u72b6\u6001");
            action.setCompletionMode("");
            action.setVerificationStatus("");
            session.currentActionId = action.getId();
            session.sessionStatus = AGENT_STATUS_IN_PROGRESS;
            session.updatedAt = LocalDateTime.now(CHINA_ZONE);
            persistSession(session);
            return createAgentBrief(session, false, latitude, longitude);
        }

        if (AGENT_STATUS_COMPLETED.equals(action.getStatus()) || AGENT_STATUS_SKIPPED.equals(action.getStatus())) {
            return createAgentBrief(session, false, latitude, longitude);
        }

        if (AGENT_STATUS_COMPLETED.equals(targetStatus)) {
            CompletionDecision decision = verifyActionCompletion(session, action, resultNote);
            action.setResultNote(decision.message);
            action.setCompletionMode(decision.mode);
            action.setVerificationStatus(decision.accepted ? "passed" : "failed");
            session.updatedAt = LocalDateTime.now(CHINA_ZONE);

            if (decision.accepted) {
                action.setStatus(AGENT_STATUS_COMPLETED);
                session.currentActionId = "";
                rebuildSessionPlan(session, false);
                updateAgentMemory(session, decision.mode);
                persistSession(session);
                return createAgentBrief(session, true, latitude, longitude);
            }

            action.setStatus(AGENT_STATUS_IN_PROGRESS);
            session.currentActionId = action.getId();
            session.sessionStatus = AGENT_STATUS_IN_PROGRESS;
            persistSession(session);
            return createAgentBrief(session, false, latitude, longitude);
        }

        action.setStatus(targetStatus);
        action.setResultNote(resultNote.isEmpty() ? defaultResultNote(targetStatus) : resultNote);
        action.setCompletionMode(AGENT_STATUS_SKIPPED.equals(targetStatus) ? "skipped" : "");
        action.setVerificationStatus(AGENT_STATUS_SKIPPED.equals(targetStatus) ? "not_applicable" : "");
        session.currentActionId = "";
        session.updatedAt = LocalDateTime.now(CHINA_ZONE);
        rebuildSessionPlan(session, false);
        if (AGENT_STATUS_SKIPPED.equals(targetStatus)) {
            updateAgentMemory(session, "skipped");
        }
        persistSession(session);
        return createAgentBrief(session, true, latitude, longitude);
    }

    private AgentRuntimeSession getOrCreateSession(String userId, String userNote) {
        AgentRuntimeSession current = loadLatestSession(userId);
        if (current == null) {
            AgentRuntimeSession created = createNewSession(userId, userNote);
            persistSession(created);
            updateAgentMemory(created, "");
            return created;
        }
        if (!userNote.isEmpty() && !userNote.equals(current.userNote)) {
            AgentRuntimeSession created = createNewSession(userId, userNote);
            persistSession(created);
            updateAgentMemory(created, "");
            return created;
        }
        current.updatedAt = LocalDateTime.now(CHINA_ZONE);
        if (!userNote.isEmpty()) {
            current.userNote = userNote;
        }
        return current;
    }

    private void rebuildSessionPlan(AgentRuntimeSession session, boolean resetExisting) {
        rebuildSessionPlan(session, resetExisting, null);
    }

    private void rebuildSessionPlan(AgentRuntimeSession session, boolean resetExisting, AgentWeatherVO weather) {
        Map<String, Object> taskBoard = taskService.getTaskBoard(session.userId);
        List<TaskBoardItemVO> allTasks = castTaskItems(taskBoard.get("allTasks"));
        List<HealthData> healthDataList = healthDataService.getByUserId(session.userId);
        AgentMemoryProfile memoryProfile = loadAgentMemoryProfile(session.userId, session.userNote);
        AgentScheduleVO schedule = studentScheduleService.getTodayContext(session.userId);
        AgentPreferenceProfile preferences = buildPlanningPreferences(session.userNote, memoryProfile, weather, schedule);
        List<AgentActionVO> baseActions = buildActionPlan(allTasks, healthDataList, preferences, memoryProfile);
        syncTaskCompletionState(session.actions, allTasks);

        if (resetExisting || session.actions == null || session.actions.isEmpty()) {
            session.actions = initializeSessionActions(baseActions);
        } else {
            session.actions = mergeSessionActions(session.actions, baseActions);
        }

        session.sessionStatus = resolveSessionStatus(session.actions);
        session.currentActionId = findCurrentActionId(session.actions);
        session.updatedAt = LocalDateTime.now(CHINA_ZONE);
        session.planVersion = session.planVersion == null ? 1 : session.planVersion + 1;
    }

    private AgentBriefVO createAgentBrief(AgentRuntimeSession session, boolean allowRefresh, Double latitude, Double longitude) {
        return createAgentBrief(session, allowRefresh, latitude, longitude, weatherService.getCurrentWeather(latitude, longitude));
    }

    private AgentBriefVO createAgentBrief(AgentRuntimeSession session, boolean allowRefresh, Double latitude, Double longitude, AgentWeatherVO weather) {
        if (session == null) {
            AgentBriefVO brief = new AgentBriefVO();
            brief.setSummary(createEmptySummary());
            brief.setSessionStatus("idle");
            brief.setWeather(weather);
            brief.setSchedule(null);
            brief.setSportPlan(mapVenueService.buildSportPlan("", latitude, longitude));
            return brief;
        }

        if (allowRefresh) {
            rebuildSessionPlan(session, false, weather);
        } else {
            session.sessionStatus = resolveSessionStatus(session.actions);
            session.currentActionId = findCurrentActionId(session.actions);
        }

        Map<String, Object> taskBoard = taskService.getTaskBoard(session.userId);
        List<HealthData> healthDataList = healthDataService.getByUserId(session.userId);
        AgentMemoryProfile memoryProfile = loadAgentMemoryProfile(session.userId, session.userNote);
        AgentScheduleVO schedule = studentScheduleService.getTodayContext(session.userId);
        AgentPreferenceProfile preferences = buildPlanningPreferences(session.userNote, memoryProfile, weather, schedule);
        List<AgentActionVO> actions = prepareActionsForView(session.actions);
        AgentSummaryVO summary = buildSummary(taskBoard, healthDataList, preferences, actions);
        decorateSummary(summary, session, actions);

        AgentBriefVO brief = new AgentBriefVO();
        brief.setSessionId(session.sessionId);
        brief.setSessionStatus(session.sessionStatus);
        brief.setCurrentActionId(session.currentActionId);
        brief.setSummary(summary);
        brief.setActions(actions);
        brief.setWeather(weather);
        brief.setSchedule(schedule);
        AgentSportPlanVO sportPlan = mapVenueService.buildSportPlan(session.userNote, latitude, longitude);
        brief.setSportPlan(sportPlan);
        brief.setEvidence(buildAgentEvidence(taskBoard, healthDataList, preferences, memoryProfile, actions, session));
        if (weather != null && !safeText(weather.getRecommendation()).isEmpty()) {
            brief.getEvidence().add("\u5929\u6c14\u7ea6\u675f\uff1a" + weather.getRecommendation());
        }
        if (schedule != null && !safeText(schedule.getRecommendation()).isEmpty()) {
            brief.getEvidence().add("\u8bfe\u8868\u8282\u594f\uff1a" + schedule.getRecommendation());
        }
        if (sportPlan != null && !safeText(sportPlan.getRecommendationReason()).isEmpty()) {
            brief.getEvidence().add("\u8fd0\u52a8\u573a\u5730\uff1a" + sportPlan.getRecommendationReason());
        }
        persistSession(session);
        return brief;
    }

    private List<AgentActionVO> initializeSessionActions(List<AgentActionVO> baseActions) {
        List<AgentActionVO> initialized = new ArrayList<>();
        int order = 1;
        for (AgentActionVO action : baseActions) {
            AgentActionVO copy = copyAction(action);
            copy.setStepOrder(order++);
            copy.setStatus(AGENT_STATUS_PENDING);
            copy.setResultNote("");
            initialized.add(copy);
            if (initialized.size() >= MAX_AGENT_ACTIONS) {
                break;
            }
        }
        return initialized;
    }

    private List<AgentActionVO> mergeSessionActions(List<AgentActionVO> existingActions, List<AgentActionVO> baseActions) {
        List<AgentActionVO> merged = new ArrayList<>();
        Set<String> usedIds = new LinkedHashSet<>();

        for (AgentActionVO existing : existingActions) {
            if (existing == null) {
                continue;
            }
            if (AGENT_STATUS_COMPLETED.equals(existing.getStatus())
                    || AGENT_STATUS_SKIPPED.equals(existing.getStatus())
                    || AGENT_STATUS_IN_PROGRESS.equals(existing.getStatus())) {
                AgentActionVO copy = copyAction(existing);
                merged.add(copy);
                usedIds.add(copy.getId());
            }
        }

        for (AgentActionVO candidate : baseActions) {
            if (candidate == null || usedIds.contains(candidate.getId())) {
                continue;
            }
            AgentActionVO copy = copyAction(candidate);
            copy.setStatus(AGENT_STATUS_PENDING);
            copy.setResultNote("");
            merged.add(copy);
            usedIds.add(copy.getId());
            if (merged.size() >= MAX_AGENT_ACTIONS) {
                break;
            }
        }

        if (merged.isEmpty()) {
            return initializeSessionActions(baseActions);
        }

        reindexActions(merged);
        return merged;
    }

    private List<AgentActionVO> prepareActionsForView(List<AgentActionVO> source) {
        List<AgentActionVO> actions = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return actions;
        }
        boolean hasInProgress = false;
        for (AgentActionVO action : source) {
            if (action != null && AGENT_STATUS_IN_PROGRESS.equals(action.getStatus())) {
                hasInProgress = true;
                break;
            }
        }

        boolean startAssigned = false;
        for (AgentActionVO action : source) {
            if (action == null) {
                continue;
            }
            AgentActionVO copy = copyAction(action);
            copy.setStatusLabel(resolveActionStatusLabel(copy.getStatus()));
            copy.setExecutionHint(resolveExecutionHint(copy));
            boolean pending = AGENT_STATUS_PENDING.equals(copy.getStatus());
            boolean inProgress = AGENT_STATUS_IN_PROGRESS.equals(copy.getStatus());
            boolean canStart = pending && !hasInProgress && !startAssigned;
            copy.setCanStart(canStart);
            copy.setCanFinish(inProgress);
            copy.setCanSkip(inProgress || canStart);
            if (canStart) {
                startAssigned = true;
            }
            actions.add(copy);
        }
        return actions;
    }

    private void decorateSummary(AgentSummaryVO summary, AgentRuntimeSession session, List<AgentActionVO> actions) {
        int total = actions.size();
        int completed = 0;
        int activeCount = 0;
        AgentActionVO currentAction = null;
        int pendingCarbon = 0;
        int pendingPoints = 0;

        for (AgentActionVO action : actions) {
            if (action == null) {
                continue;
            }
            if (AGENT_STATUS_COMPLETED.equals(action.getStatus())) {
                completed++;
            } else if (AGENT_STATUS_IN_PROGRESS.equals(action.getStatus())) {
                currentAction = action;
                activeCount++;
            }

            if (!AGENT_STATUS_COMPLETED.equals(action.getStatus()) && !AGENT_STATUS_SKIPPED.equals(action.getStatus())) {
                pendingCarbon += action.getEstimatedCarbonSaving() == null ? 0 : action.getEstimatedCarbonSaving();
                pendingPoints += action.getEstimatedPoints() == null ? 0 : action.getEstimatedPoints();
            }
        }

        summary.setCompletionLabel(completed + "/" + total);
        summary.setEstimatedCarbonSaving(pendingCarbon);
        summary.setEstimatedPoints(pendingPoints);
        summary.setUpdatedAt(session.updatedAt == null ? LocalDateTime.now(CHINA_ZONE).format(TIME_FORMATTER)
                : session.updatedAt.format(TIME_FORMATTER));

        if (currentAction != null) {
            summary.setTitle("\u6b63\u5728\u6267\u884c\uff1a" + currentAction.getTitle());
            summary.setStatus(AGENT_STATUS_IN_PROGRESS);
            summary.setReason("\u5148\u5b8c\u6210\u5f53\u524d\u8fd9\u4e00\u6b65\uff0cAgent \u4f1a\u5728\u4f60\u56de\u6765\u540e\u7acb\u5373\u91cd\u6392\u540e\u7eed\u52a8\u4f5c\u3002");
            summary.setFocusLabel("\u6267\u884c\u4e2d");
            return;
        }

        if (total > 0 && completed == total) {
            summary.setTitle("\u4eca\u65e5 Agent \u95ed\u73af\u5df2\u5b8c\u6210");
            summary.setStatus(AGENT_STATUS_COMPLETED);
            summary.setReason("\u672c\u8f6e\u52a8\u4f5c\u5df2\u5168\u90e8\u5904\u7406\uff0c\u53ef\u4ee5\u518d\u8865\u5145\u65b0\u60c5\u51b5\u8ba9 Agent \u751f\u6210\u4e0b\u4e00\u8f6e\u8ba1\u5212\u3002");
            summary.setFocusLabel("\u5df2\u6536\u5b98");
            return;
        }

        AgentActionVO nextAction = findNextPendingAction(actions);
        if (nextAction != null) {
            summary.setTitle("\u4e0b\u4e00\u6b65\uff1a" + nextAction.getTitle());
            summary.setStatus("ready");
            summary.setReason("\u5148\u6267\u884c\u7b2c " + nextAction.getStepOrder() + " \u6b65\uff0c\u6267\u884c\u540e Agent \u4f1a\u7ed3\u5408\u6700\u65b0\u72b6\u6001\u91cd\u65b0\u6392\u5217\u540e\u7eed\u8ba1\u5212\u3002");
            summary.setFocusLabel("\u5f85\u6267\u884c");
        }
    }

    private List<String> buildAgentEvidence(Map<String, Object> taskBoard, List<HealthData> healthDataList,
                                            AgentPreferenceProfile preferences, AgentMemoryProfile memoryProfile,
                                            List<AgentActionVO> actions,
                                            AgentRuntimeSession session) {
        List<String> evidence = buildEvidence(taskBoard, healthDataList, preferences, memoryProfile, actions);
        int completed = 0;
        int skipped = 0;
        int inProgress = 0;
        for (AgentActionVO action : actions) {
            if (action == null) {
                continue;
            }
            if (AGENT_STATUS_COMPLETED.equals(action.getStatus())) {
                completed++;
            } else if (AGENT_STATUS_SKIPPED.equals(action.getStatus())) {
                skipped++;
            } else if (AGENT_STATUS_IN_PROGRESS.equals(action.getStatus())) {
                inProgress++;
            }
        }
        evidence.add("\u4f1a\u8bdd\u7f16\u53f7\uff1a" + session.sessionId);
        evidence.add("\u52a8\u4f5c\u72b6\u6001\uff1a\u5b8c\u6210 " + completed + "\u9879\uff0c\u6267\u884c\u4e2d " + inProgress + "\u9879\uff0c\u8df3\u8fc7 " + skipped + "\u9879");
        if (!safeText(session.userNote).isEmpty()) {
            evidence.add("\u7528\u6237\u8865\u5145\uff1a" + session.userNote);
        }
        return evidence;
    }

    private void syncTaskCompletionState(List<AgentActionVO> actions, List<TaskBoardItemVO> allTasks) {
        if (actions == null || actions.isEmpty() || allTasks == null || allTasks.isEmpty()) {
            return;
        }
        Map<String, Boolean> completionMap = new LinkedHashMap<>();
        for (TaskBoardItemVO item : allTasks) {
            completionMap.put(item.getTaskCode(), Boolean.TRUE.equals(item.getCompleted()));
        }
        for (AgentActionVO action : actions) {
            if (action == null || safeText(action.getTaskCode()).isEmpty()) {
                continue;
            }
            if (Boolean.TRUE.equals(completionMap.get(action.getTaskCode()))
                    && !AGENT_STATUS_SKIPPED.equals(action.getStatus())) {
                action.setStatus(AGENT_STATUS_COMPLETED);
                action.setResultNote("\u7cfb\u7edf\u5df2\u68c0\u6d4b\u5230\u5bf9\u5e94\u4efb\u52a1\u5df2\u8fbe\u6807\uff0c\u8be5\u6b65\u9a8c\u8bc1\u901a\u8fc7");
                action.setCompletionMode(AGENT_COMPLETION_VERIFIED);
                action.setVerificationStatus("passed");
            }
        }
    }

    private String resolveSessionStatus(List<AgentActionVO> actions) {
        boolean hasInProgress = false;
        boolean hasPending = false;
        for (AgentActionVO action : actions) {
            if (action == null) {
                continue;
            }
            if (AGENT_STATUS_IN_PROGRESS.equals(action.getStatus())) {
                hasInProgress = true;
            } else if (AGENT_STATUS_PENDING.equals(action.getStatus())) {
                hasPending = true;
            }
        }
        if (hasInProgress) {
            return AGENT_STATUS_IN_PROGRESS;
        }
        if (hasPending) {
            return "ready";
        }
        return AGENT_STATUS_COMPLETED;
    }

    private String findCurrentActionId(List<AgentActionVO> actions) {
        for (AgentActionVO action : actions) {
            if (action != null && AGENT_STATUS_IN_PROGRESS.equals(action.getStatus())) {
                return action.getId();
            }
        }
        return "";
    }

    private void clearInProgressState(List<AgentActionVO> actions, String exceptActionId) {
        for (AgentActionVO action : actions) {
            if (action == null) {
                continue;
            }
            if (AGENT_STATUS_IN_PROGRESS.equals(action.getStatus())
                    && !Objects.equals(action.getId(), exceptActionId)) {
                action.setStatus(AGENT_STATUS_PENDING);
                if ("\u5df2\u8fdb\u5165\u6267\u884c\u72b6\u6001".equals(action.getResultNote())) {
                    action.setResultNote("");
                }
            }
        }
    }

    private AgentActionVO findAction(List<AgentActionVO> actions, String actionId) {
        for (AgentActionVO action : actions) {
            if (action != null && Objects.equals(action.getId(), actionId)) {
                return action;
            }
        }
        return null;
    }

    private AgentActionVO findNextPendingAction(List<AgentActionVO> actions) {
        for (AgentActionVO action : actions) {
            if (action != null && AGENT_STATUS_PENDING.equals(action.getStatus())) {
                return action;
            }
        }
        return null;
    }

    private CompletionDecision verifyActionCompletion(AgentRuntimeSession session, AgentActionVO action, String resultNote) {
        String taskCode = safeText(action.getTaskCode());
        if (requiresClaimCompletion(taskCode)) {
            return createClaimDecision(resultNote,
                    "\u8fd9\u4e00\u6b65\u6682\u65f6\u65e0\u6cd5\u81ea\u52a8\u9a8c\u8bc1\uff0c\u5df2\u6309\u7528\u6237\u7533\u62a5\u8bb0\u4e3a\u5b8c\u6210");
        }

        String verifierRoute = resolveVerifierRoute(action);
        if ("health_profile".equals(verifierRoute)) {
            return verifyHealthProfileCompletion(session);
        }

        if ("task_board".equals(verifierRoute)) {
            return verifyTaskBoardActionCompletion(session, action);
        }

        return createClaimDecision(resultNote,
                "\u8fd9\u4e00\u6b65\u672a\u63a5\u5165\u81ea\u52a8\u6821\u9a8c\uff0c\u5df2\u6309\u7528\u6237\u7533\u62a5\u8bb0\u5f55\u5b8c\u6210");
    }

    private String resolveVerifierRoute(AgentActionVO action) {
        String taskCode = safeText(action.getTaskCode());
        String actionPath = safeText(action.getActionPath());
        if ("HEALTH_PROFILE".equals(taskCode) || "/pages/healthData/healthData".equals(actionPath)) {
            return "health_profile";
        }
        if (hasTaskBoardMetric(taskCode)) {
            return "task_board";
        }
        if (("navigate".equalsIgnoreCase(safeText(action.getActionType()))
                || "tab".equalsIgnoreCase(safeText(action.getActionType())))
                && ("/pages/stepCount/stepCount".equals(actionPath)
                || "/pages/sportRecord/sportRecord".equals(actionPath)
                || "/pages/mall/mall".equals(actionPath))) {
            return "task_board";
        }
        return "claim";
    }

    private CompletionDecision verifyHealthProfileCompletion(AgentRuntimeSession session) {
        List<HealthData> healthDataList = healthDataService.getByUserId(session.userId);
        if (healthDataList == null || healthDataList.isEmpty()) {
            return new CompletionDecision(
                    false,
                    AGENT_COMPLETION_REJECTED,
                    "\u6682\u672a\u68c0\u6d4b\u5230\u65b0\u7684\u5065\u5eb7\u6863\u6848\u8bb0\u5f55\uff0c\u8bf7\u4fdd\u5b58\u540e\u518d\u70b9\u51fb\u201c\u6211\u5df2\u5b8c\u6210\u201d"
            );
        }
        LocalDateTime sessionTime = session.createdAt == null ? LocalDateTime.now(CHINA_ZONE).minusDays(1) : session.createdAt.minusMinutes(1);
        for (HealthData data : healthDataList) {
            LocalDateTime recordTime = resolveHealthRecordTime(data);
            if (recordTime != null && !recordTime.isBefore(sessionTime)) {
                return new CompletionDecision(
                        true,
                        AGENT_COMPLETION_VERIFIED,
                        "\u7cfb\u7edf\u5df2\u68c0\u6d4b\u5230\u65b0\u7684\u5065\u5eb7\u6863\u6848\u8bb0\u5f55\uff0c\u8be5\u6b65\u9a8c\u8bc1\u901a\u8fc7"
                );
            }
        }
        return new CompletionDecision(
                false,
                AGENT_COMPLETION_REJECTED,
                "\u68c0\u6d4b\u5230\u4f60\u6709\u5386\u53f2\u5065\u5eb7\u6570\u636e\uff0c\u4f46\u672c\u6b21\u4f1a\u8bdd\u540e\u8fd8\u6ca1\u6709\u65b0\u7684\u4fdd\u5b58\u8bb0\u5f55"
        );
    }

    private CompletionDecision verifyTaskBoardActionCompletion(AgentRuntimeSession session, AgentActionVO action) {
        String taskCode = safeText(action.getTaskCode());
        if (taskCode.isEmpty()) {
            return new CompletionDecision(
                    false,
                    AGENT_COMPLETION_REJECTED,
                    "\u6682\u65e0\u53ef\u6821\u9a8c\u7684\u4efb\u52a1\u6807\u8bc6\uff0c\u8bf7\u5148\u5728\u5bf9\u5e94\u9875\u9762\u5b8c\u6210\u64cd\u4f5c"
            );
        }
        Map<String, Object> taskBoard = taskService.getTaskBoard(session.userId, Collections.singletonList(taskCode));
        TaskBoardItemVO item = findTaskBoardItem(castTaskItems(taskBoard.get("allTasks")), taskCode);
        if (item == null) {
            return new CompletionDecision(
                    false,
                    AGENT_COMPLETION_REJECTED,
                    "\u6682\u672a\u627e\u5230\u5bf9\u5e94\u4efb\u52a1\u7684\u5b9e\u65f6\u8fdb\u5ea6\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5"
            );
        }
        if (Boolean.TRUE.equals(item.getCompleted())) {
            return new CompletionDecision(
                    true,
                    AGENT_COMPLETION_VERIFIED,
                    "\u7cfb\u7edf\u5df2\u68c0\u6d4b\u5230\u5bf9\u5e94\u4efb\u52a1\u5df2\u8fbe\u6807\uff0c\u8be5\u6b65\u9a8c\u8bc1\u901a\u8fc7"
            );
        }
        return new CompletionDecision(
                false,
                AGENT_COMPLETION_REJECTED,
                buildPendingTaskMessage(action, item)
        );
    }

    private CompletionDecision createClaimDecision(String resultNote, String fallbackMessage) {
        return new CompletionDecision(
                true,
                AGENT_COMPLETION_CLAIMED,
                resultNote.isEmpty() ? fallbackMessage : resultNote
        );
    }

    private boolean hasTaskBoardMetric(String taskCode) {
        return "DAILY_STEP_6000".equals(taskCode)
                || "DAILY_SPORT_2KM".equals(taskCode)
                || "DAILY_CHECKIN_1".equals(taskCode)
                || "WEEKLY_STEP_50000".equals(taskCode)
                || "WEEKLY_SPORT_3_TIMES".equals(taskCode)
                || "WEEKLY_REDEEM_1".equals(taskCode);
    }

    private boolean requiresClaimCompletion(String taskCode) {
        return "MAINTAIN_WALK".equals(taskCode)
                || "MAINTAIN_INDOOR".equals(taskCode)
                || taskCode.isEmpty();
    }

    private TaskBoardItemVO findTaskBoardItem(List<TaskBoardItemVO> items, String taskCode) {
        for (TaskBoardItemVO item : items) {
            if (item != null && Objects.equals(taskCode, item.getTaskCode())) {
                return item;
            }
        }
        return null;
    }

    private String buildPendingTaskMessage(AgentActionVO action, TaskBoardItemVO item) {
        String taskCode = safeText(item.getTaskCode());
        double currentValue = item.getCurrentValue() == null ? 0D : item.getCurrentValue();
        double targetValue = item.getTargetValue() == null ? 0D : item.getTargetValue();
        double remaining = Math.max(targetValue - currentValue, 0D);

        if ("DAILY_STEP_6000".equals(taskCode) || "WEEKLY_STEP_50000".equals(taskCode)) {
            return "\u5df2\u68c0\u6d4b\u5230\u5f53\u524d\u6b65\u6570\u8fdb\u5ea6 " + formatInt(currentValue) + " / "
                    + formatInt(targetValue) + "\u6b65\uff0c\u8fd8\u5dee " + formatInt(remaining)
                    + "\u6b65\u624d\u80fd\u901a\u8fc7\u8fd9\u4e00\u6b65\u9a8c\u8bc1";
        }
        if ("DAILY_SPORT_2KM".equals(taskCode)) {
            return "\u5df2\u68c0\u6d4b\u5230\u8fd0\u52a8\u8fdb\u5ea6 " + formatDecimal(currentValue) + " / "
                    + formatDecimal(targetValue) + "km\uff0c\u8fd8\u5dee " + formatDecimal(remaining)
                    + "km \u624d\u80fd\u5b8c\u6210\u8fd9\u4e00\u6b65";
        }
        if ("WEEKLY_SPORT_3_TIMES".equals(taskCode) || "WEEKLY_REDEEM_1".equals(taskCode) || "DAILY_CHECKIN_1".equals(taskCode)) {
            return "\u5df2\u68c0\u6d4b\u5230\u5f53\u524d\u8fdb\u5ea6 " + formatInt(currentValue) + " / "
                    + formatInt(targetValue) + "\u6b21\uff0c\u8fd8\u5dee " + formatInt(remaining)
                    + "\u6b21\u624d\u80fd\u901a\u8fc7\u6821\u9a8c";
        }
        if (!safeText(action.getActionPath()).isEmpty()) {
            return "\u6682\u672a\u68c0\u6d4b\u5230\u8fd9\u4e00\u6b65\u7684\u5b8c\u6210\u8bb0\u5f55\uff0c\u8bf7\u5148\u5728\u5bf9\u5e94\u9875\u9762\u5b8c\u6210\u64cd\u4f5c";
        }
        return "\u6682\u672a\u68c0\u6d4b\u5230\u8fd9\u4e00\u6b65\u7684\u5b8c\u6210\u8bb0\u5f55";
    }

    private LocalDateTime resolveHealthRecordTime(HealthData data) {
        if (data == null) {
            return null;
        }
        if (data.getUpdatedAt() != null) {
            return toLocalDateTime(data.getUpdatedAt());
        }
        if (data.getCreatedAt() != null) {
            return toLocalDateTime(data.getCreatedAt());
        }
        if (data.getRecordDate() != null) {
            LocalDateTime value = toLocalDateTime(data.getRecordDate());
            return value == null ? null : value.plusHours(23).plusMinutes(59).plusSeconds(59);
        }
        return null;
    }

    private AgentRuntimeSession loadLatestSession(String userId) {
        AgentSession entity = agentSessionMapper.selectLatestByUsername(userId);
        if (entity == null) {
            return null;
        }
        List<AgentActionLog> logs = agentActionLogMapper.selectBySessionId(entity.getSessionId());
        AgentRuntimeSession session = new AgentRuntimeSession();
        session.sessionId = entity.getSessionId();
        session.userId = entity.getUsername();
        session.userNote = safeText(entity.getUserNote());
        session.sessionStatus = safeText(entity.getSessionStatus()).isEmpty() ? "ready" : entity.getSessionStatus();
        session.currentActionId = safeText(entity.getCurrentActionId());
        session.planVersion = entity.getPlanVersion() == null ? 1 : entity.getPlanVersion();
        session.createdAt = toLocalDateTime(entity.getCreateTime());
        session.updatedAt = toLocalDateTime(entity.getUpdateTime());
        session.actions = new ArrayList<>();
        for (AgentActionLog log : logs) {
            session.actions.add(toActionVO(log));
        }
        if (session.createdAt == null) {
            session.createdAt = LocalDateTime.now(CHINA_ZONE);
        }
        if (session.updatedAt == null) {
            session.updatedAt = LocalDateTime.now(CHINA_ZONE);
        }
        return session;
    }

    private AgentRuntimeSession createNewSession(String userId, String userNote) {
        AgentRuntimeSession session = new AgentRuntimeSession();
        LocalDateTime now = LocalDateTime.now(CHINA_ZONE);
        session.sessionId = UUID.randomUUID().toString().replace("-", "");
        session.userId = userId;
        session.userNote = safeText(userNote);
        session.sessionStatus = "ready";
        session.currentActionId = "";
        session.createdAt = now;
        session.updatedAt = now;
        session.planVersion = 0;
        session.actions = new ArrayList<>();
        return session;
    }

    private void persistSession(AgentRuntimeSession session) {
        if (session == null || safeText(session.sessionId).isEmpty() || safeText(session.userId).isEmpty()) {
            return;
        }
        AgentSession entity = new AgentSession();
        entity.setSessionId(session.sessionId);
        entity.setUsername(session.userId);
        entity.setSessionStatus(safeText(session.sessionStatus).isEmpty() ? "ready" : session.sessionStatus);
        entity.setCurrentActionId(safeText(session.currentActionId));
        entity.setUserNote(safeText(session.userNote));
        entity.setPlanVersion(session.planVersion == null || session.planVersion < 1 ? 1 : session.planVersion);
        entity.setLastActiveTime(toDate(session.updatedAt));

        AgentSession existing = agentSessionMapper.selectBySessionId(session.sessionId);
        if (existing == null) {
            agentSessionMapper.insert(entity);
        } else {
            agentSessionMapper.updateBySessionId(entity);
        }

        agentActionLogMapper.deleteBySessionId(session.sessionId);
        if (session.actions == null) {
            return;
        }
        for (AgentActionVO action : session.actions) {
            if (action == null) {
                continue;
            }
            agentActionLogMapper.insert(toActionLog(session, action));
        }
    }

    private void updateAgentMemory(AgentRuntimeSession session, String mode) {
        if (session == null || safeText(session.userId).isEmpty()) {
            return;
        }
        AgentMemory memory = agentMemoryMapper.selectByUsername(session.userId);
        if (memory == null) {
            memory = new AgentMemory();
            memory.setUsername(session.userId);
            memory.setVerifiedCompletionCount(0);
            memory.setClaimedCompletionCount(0);
            memory.setSkippedCount(0);
        }
        memory.setLastSessionId(session.sessionId);
        memory.setLastUserNote(safeText(session.userNote));
        memory.setLastActiveTime(toDate(session.updatedAt));

        if (AGENT_COMPLETION_VERIFIED.equals(mode)) {
            memory.setVerifiedCompletionCount(safeInt(memory.getVerifiedCompletionCount()) + 1);
        } else if (AGENT_COMPLETION_CLAIMED.equals(mode)) {
            memory.setClaimedCompletionCount(safeInt(memory.getClaimedCompletionCount()) + 1);
        } else if (AGENT_STATUS_SKIPPED.equals(mode)) {
            memory.setSkippedCount(safeInt(memory.getSkippedCount()) + 1);
        }

        if (memory.getId() == null) {
            agentMemoryMapper.insert(memory);
        } else {
            agentMemoryMapper.updateByUsername(memory);
        }
    }

    private AgentActionVO toActionVO(AgentActionLog log) {
        AgentActionVO action = new AgentActionVO();
        action.setId(log.getActionId());
        action.setTaskCode(log.getTaskCode());
        action.setStepOrder(log.getStepOrder());
        action.setTitle(log.getTitle());
        action.setReason(log.getReason());
        action.setPriorityTag(log.getPriorityTag());
        action.setDurationMinutes(log.getDurationMinutes());
        action.setEstimatedCarbonSaving(log.getEstimatedCarbonSaving());
        action.setEstimatedPoints(log.getEstimatedPoints());
        action.setActionText(log.getActionText());
        action.setActionPath(log.getActionPath());
        action.setActionType(log.getActionType());
        action.setStatus(log.getStatus());
        action.setResultNote(safeText(log.getResultNote()));
        action.setCompletionMode(safeText(log.getCompletionMode()));
        action.setVerificationStatus(safeText(log.getVerificationStatus()));
        action.setExecutionHint(safeText(log.getExecutionHint()));
        return action;
    }

    private AgentActionLog toActionLog(AgentRuntimeSession session, AgentActionVO action) {
        AgentActionLog log = new AgentActionLog();
        log.setSessionId(session.sessionId);
        log.setActionId(action.getId());
        log.setTaskCode(safeText(action.getTaskCode()));
        log.setStepOrder(action.getStepOrder());
        log.setTitle(action.getTitle());
        log.setReason(action.getReason());
        log.setPriorityTag(action.getPriorityTag());
        log.setDurationMinutes(action.getDurationMinutes());
        log.setEstimatedCarbonSaving(action.getEstimatedCarbonSaving());
        log.setEstimatedPoints(action.getEstimatedPoints());
        log.setActionText(action.getActionText());
        log.setActionPath(action.getActionPath());
        log.setActionType(action.getActionType());
        log.setStatus(action.getStatus());
        log.setResultNote(safeText(action.getResultNote()));
        log.setCompletionMode(safeText(action.getCompletionMode()));
        log.setVerificationStatus(safeText(action.getVerificationStatus()));
        log.setExecutionHint(resolveExecutionHint(action));
        return log;
    }

    private LocalDateTime toLocalDateTime(Date value) {
        if (value == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(value.getTime()), CHINA_ZONE);
    }

    private Date toDate(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atZone(CHINA_ZONE).toInstant());
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private AgentActionVO copyAction(AgentActionVO source) {
        AgentActionVO target = new AgentActionVO();
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setReason(source.getReason());
        target.setTaskCode(source.getTaskCode());
        target.setPriorityTag(source.getPriorityTag());
        target.setDurationMinutes(source.getDurationMinutes());
        target.setEstimatedCarbonSaving(source.getEstimatedCarbonSaving());
        target.setEstimatedPoints(source.getEstimatedPoints());
        target.setActionText(source.getActionText());
        target.setActionPath(source.getActionPath());
        target.setActionType(source.getActionType());
        target.setStepOrder(source.getStepOrder());
        target.setStatus(source.getStatus());
        target.setStatusLabel(source.getStatusLabel());
        target.setExecutionHint(source.getExecutionHint());
        target.setResultNote(source.getResultNote());
        target.setCompletionMode(source.getCompletionMode());
        target.setVerificationStatus(source.getVerificationStatus());
        target.setCanStart(source.getCanStart());
        target.setCanFinish(source.getCanFinish());
        target.setCanSkip(source.getCanSkip());
        return target;
    }

    private void reindexActions(List<AgentActionVO> actions) {
        int order = 1;
        for (AgentActionVO action : actions) {
            if (action != null) {
                action.setStepOrder(order++);
            }
        }
    }

    private String resolveActionStatusLabel(String status) {
        if (AGENT_STATUS_IN_PROGRESS.equals(status)) {
            return "\u6267\u884c\u4e2d";
        }
        if (AGENT_STATUS_COMPLETED.equals(status)) {
            return "\u5df2\u5b8c\u6210";
        }
        if (AGENT_STATUS_SKIPPED.equals(status)) {
            return "\u5df2\u8df3\u8fc7";
        }
        return "\u5f85\u6267\u884c";
    }

    private String resolveExecutionHint(AgentActionVO action) {
        if (AGENT_STATUS_COMPLETED.equals(action.getStatus())) {
            return safeText(action.getResultNote()).isEmpty() ? "\u8fd9\u4e00\u6b65\u5df2\u8bb0\u5f55\u5b8c\u6210" : action.getResultNote();
        }
        if (AGENT_STATUS_SKIPPED.equals(action.getStatus())) {
            return safeText(action.getResultNote()).isEmpty() ? "\u8fd9\u4e00\u6b65\u5df2\u8df3\u8fc7\uff0cAgent \u4f1a\u4fdd\u7559\u540e\u7eed\u8c03\u6574\u7a7a\u95f4" : action.getResultNote();
        }
        if (AGENT_STATUS_IN_PROGRESS.equals(action.getStatus())) {
            return "\u5148\u53bb\u5b8c\u6210\u9875\u9762\u64cd\u4f5c\uff0c\u56de\u6765\u540e\u70b9\u51fb\u201c\u5df2\u5b8c\u6210\u201d";
        }
        return "\u5efa\u8bae\u6309\u987a\u5e8f\u6267\u884c\uff0c\u8fd9\u6837 Agent \u80fd\u66f4\u51c6\u786e\u5730\u8ffd\u8e2a\u8fdb\u5ea6";
    }

    private String defaultResultNote(String status) {
        if (AGENT_STATUS_COMPLETED.equals(status)) {
            return "\u7528\u6237\u5df2\u786e\u8ba4\u5b8c\u6210\u8be5\u6b65";
        }
        if (AGENT_STATUS_SKIPPED.equals(status)) {
            return "\u7528\u6237\u4e3b\u52a8\u8df3\u8fc7\u8be5\u6b65";
        }
        return "";
    }

    private ArrayNode createMessages(String systemPrompt, String userPrompt) {
        ArrayNode messages = mapper.createArrayNode();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", userPrompt));
        return messages;
    }

    private ObjectNode createMessage(String role, String content) {
        ObjectNode message = mapper.createObjectNode();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String requestSuggestion(ArrayNode messages, String unavailableMessage) throws IOException {
        ObjectNode body = mapper.createObjectNode();
        body.put("model", MODEL_NAME);
        body.put("temperature", 0.3);
        body.put("top_p", 0.8);
        body.set("messages", messages);

        RequestBody requestBody = RequestBody.create(
                mapper.writeValueAsString(body),
                JSON_MEDIA_TYPE
        );

        Request request = new Request.Builder()
                .url(endpoint)
                .header("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String responseText = responseBody == null ? "" : responseBody.string();
            if (!response.isSuccessful()) {
                return unavailableMessage;
            }

            JsonNode root = mapper.readTree(responseText);
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            String content = contentNode.asText("");
            if (content == null || content.trim().isEmpty()) {
                return unavailableMessage;
            }
            return content;
        }
    }

    private String buildHealthSummary(List<HealthData> healthDataList) {
        if (healthDataList == null || healthDataList.isEmpty()) {
            return "\u6682\u65e0\u5065\u5eb7\u6570\u636e\u8bb0\u5f55\uff0c\u8bf7\u7ed9\u51fa\u4fdd\u5b88\u3001\u65e5\u5e38\u3001\u5bb9\u6613\u6267\u884c\u7684\u5065\u5eb7\u5efa\u8bae\u3002";
        }

        HealthData latest = healthDataList.get(0);
        StringBuilder summary = new StringBuilder();
        summary.append("\u6700\u65b0\u5065\u5eb7\u6570\u636e\uff1a");

        appendMetric(summary, "\u8eab\u9ad8", latest.getHeight(), "cm");
        appendMetric(summary, "\u4f53\u91cd", latest.getWeight(), "kg");
        appendMetric(summary, "BMI", latest.getBmi(), "");
        appendMetric(summary, "\u8840\u538b", latest.getBloodPressure(), "");
        appendMetric(summary, "\u5fc3\u7387", latest.getHeartRate(), "\u6b21/\u5206");
        appendMetric(summary, "\u8840\u7cd6", latest.getBloodSugar(), "mmol/L");

        if (healthDataList.size() >= 2) {
            HealthData previous = healthDataList.get(1);
            if (latest.getWeight() != null && previous.getWeight() != null) {
                BigDecimal diff = latest.getWeight().subtract(previous.getWeight());
                if (diff.compareTo(BigDecimal.ZERO) > 0) {
                    summary.append("\u4f53\u91cd\u8f83\u4e0a\u6b21\u4e0a\u5347").append(diff).append("kg\u3002");
                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                    summary.append("\u4f53\u91cd\u8f83\u4e0a\u6b21\u4e0b\u964d").append(diff.abs()).append("kg\u3002");
                }
            }
        }

        return summary.toString();
    }

    private void appendMetric(StringBuilder summary, String label, Object value, String unit) {
        if (value == null) {
            return;
        }
        summary.append(label).append(value).append(unit).append("\u3002");
    }

    private String normalizeSuggestion(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        String normalized = content.replace("\r\n", "\n").replace('\r', '\n');
        String[] rawLines = normalized.split("\n");
        List<String> cleanedLines = new ArrayList<>();

        for (String rawLine : rawLines) {
            String line = cleanupLine(rawLine);
            if (!line.isEmpty()) {
                cleanedLines.add(line);
            }
        }

        if (cleanedLines.isEmpty()) {
            String compact = cleanupLine(normalized);
            return compact.isEmpty() ? "" : compact;
        }

        return String.join("\n", cleanedLines);
    }

    private String cleanupLine(String rawLine) {
        if (rawLine == null) {
            return "";
        }

        String line = rawLine.trim();
        if (line.isEmpty()) {
            return "";
        }
        if (line.matches("^[-*_#>`~\\s]+$")) {
            return "";
        }

        line = line.replaceAll("^#{1,6}\\s*", "");
        line = line.replaceAll("^[-*]+\\s*", "");
        line = line.replaceAll("^\\d+[.)]\\s*", "");
        line = line.replace("**", "");
        line = line.replace("__", "");
        line = line.replace("`", "");
        line = line.replace("#", "");
        line = line.replace("*", "");
        line = line.replaceAll("\\s{2,}", " ");
        return line.trim();
    }

    private String safeText(String text) {
        return text == null ? "" : text.trim();
    }

    private AgentSummaryVO createEmptySummary() {
        AgentSummaryVO summary = new AgentSummaryVO();
        summary.setTitle("\u4eca\u65e5\u884c\u52a8\u5de5\u4f5c\u53f0");
        summary.setStatus("idle");
        summary.setReason("\u6682\u65e0\u53ef\u7528\u7684\u7528\u6237\u6570\u636e");
        summary.setFocusLabel("\u5f85\u51c6\u5907");
        summary.setCompletionLabel("0/0");
        summary.setEstimatedCarbonSaving(0);
        summary.setEstimatedPoints(0);
        summary.setUpdatedAt(LocalDateTime.now(CHINA_ZONE).format(TIME_FORMATTER));
        return summary;
    }

    private AgentSummaryVO buildSummary(Map<String, Object> taskBoard, List<HealthData> healthDataList,
                                        AgentPreferenceProfile preferences, List<AgentActionVO> actions) {
        AgentSummaryVO summary = createEmptySummary();
        Map<String, Object> summaryMap = castMap(taskBoard.get("summary"));
        int dailyCompleted = toInt(summaryMap.get("dailyCompleted"));
        int dailyTotal = toInt(summaryMap.get("dailyTotal"));
        int weeklyCompleted = toInt(summaryMap.get("weeklyCompleted"));
        int weeklyTotal = toInt(summaryMap.get("weeklyTotal"));
        int totalCarbon = actions.stream()
                .map(AgentActionVO::getEstimatedCarbonSaving)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        int totalPoints = actions.stream()
                .map(AgentActionVO::getEstimatedPoints)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        summary.setEstimatedCarbonSaving(totalCarbon);
        summary.setEstimatedPoints(totalPoints);
        summary.setCompletionLabel((dailyCompleted + weeklyCompleted) + "/" + (dailyTotal + weeklyTotal));
        summary.setUpdatedAt(LocalDateTime.now(CHINA_ZONE).format(TIME_FORMATTER));

        if (preferences.hasStrongPreference()) {
            summary.setTitle(buildPreferenceTitle(preferences));
            summary.setStatus("preference_adapted");
            summary.setReason(buildPreferenceReason(preferences, actions));
            summary.setFocusLabel(preferences.focusLabel);
            return summary;
        }

        if (dailyCompleted < dailyTotal) {
            summary.setTitle("\u4eca\u65e5\u5148\u5b8c\u6210\u65e5\u5e38\u4efb\u52a1");
            summary.setStatus("daily_gap");
            summary.setReason("\u4f60\u4eca\u5929\u8fd8\u6709" + (dailyTotal - dailyCompleted) + "\u4e2a\u65e5\u5e38\u4efb\u52a1\u672a\u5b8c\u6210\uff0c\u5148\u628a\u6700\u5bb9\u6613\u843d\u5730\u7684\u52a8\u4f5c\u505a\u6389\u3002");
            summary.setFocusLabel("\u65e5\u5e38\u4efb\u52a1");
            return summary;
        }

        if (weeklyCompleted < weeklyTotal) {
            summary.setTitle("\u4eca\u65e5\u63a8\u8fdb\u672c\u5468\u8fdb\u5ea6");
            summary.setStatus("weekly_push");
            summary.setReason("\u65e5\u5e38\u76ee\u6807\u5df2\u7ecf\u7a33\u4f4f\uff0c\u73b0\u5728\u66f4\u503c\u5f97\u63d0\u524d\u8865\u9f50\u672c\u5468\u6311\u6218\u3002");
            summary.setFocusLabel("\u672c\u5468\u6311\u6218");
            return summary;
        }

        if (healthDataList == null || healthDataList.isEmpty()) {
            summary.setTitle("\u8865\u5168\u5065\u5eb7\u6863\u6848\uff0c\u8ba9\u5efa\u8bae\u66f4\u51c6");
            summary.setStatus("health_missing");
            summary.setReason("\u5f53\u524d\u884c\u52a8\u4efb\u52a1\u5df2\u8fbe\u6807\uff0c\u4e0b\u4e00\u6b65\u5efa\u8bae\u8865\u5168\u5065\u5eb7\u6570\u636e\uff0c\u8ba9 Agent \u80fd\u505a\u66f4\u5177\u4f53\u7684\u8282\u594f\u8c03\u5ea6\u3002");
            summary.setFocusLabel("\u8865\u5168\u6863\u6848");
            return summary;
        }

        summary.setTitle("\u4eca\u65e5\u4fdd\u6301\u7eff\u8272\u8282\u594f");
        summary.setStatus("steady");
        summary.setReason("\u4f60\u7684\u57fa\u672c\u76ee\u6807\u8dd1\u5f97\u6bd4\u8f83\u7a33\uff0c\u4eca\u5929\u66f4\u9002\u5408\u505a\u4e24\u5230\u4e09\u4e2a\u4f4e\u6210\u672c\u7684\u7ef4\u6301\u578b\u52a8\u4f5c\u3002");
        summary.setFocusLabel("\u4fdd\u6301\u8282\u594f");
        return summary;
    }

    private List<AgentActionVO> buildActionPlan(List<TaskBoardItemVO> allTasks, List<HealthData> healthDataList,
                                                AgentPreferenceProfile preferences, AgentMemoryProfile memoryProfile) {
        List<PlannedAction> candidates = new ArrayList<>();
        for (TaskBoardItemVO item : allTasks) {
            if (!Boolean.TRUE.equals(item.getCompleted())) {
                AgentActionVO action = toAgentAction(item, preferences);
                candidates.add(new PlannedAction(action, scoreAction(action, preferences, memoryProfile)));
            }
        }

        if ((healthDataList == null || healthDataList.isEmpty()) && (preferences.healthFirst || preferences.preferIndoor || preferences.energyLow)) {
            AgentActionVO healthProfile = createHealthProfileAction(preferences);
            candidates.add(new PlannedAction(healthProfile, scoreAction(healthProfile, preferences, memoryProfile) + 60D));
        }

        candidates.sort((left, right) -> Double.compare(right.score, left.score));

        List<AgentActionVO> actions = new ArrayList<>();
        for (PlannedAction candidate : candidates) {
            if (containsAction(actions, candidate.action.getId())) {
                continue;
            }
            actions.add(candidate.action);
            if (actions.size() >= 3) {
                break;
            }
        }

        if ((healthDataList == null || healthDataList.isEmpty()) && actions.size() < 3) {
            AgentActionVO healthProfile = createHealthProfileAction(preferences);
            if (!containsAction(actions, healthProfile.getId())) {
                actions.add(healthProfile);
            }
        }

        if (actions.isEmpty()) {
            actions.add(createMaintenanceAction(preferences));
        }

        return actions;
    }

    private List<String> buildEvidence(Map<String, Object> taskBoard, List<HealthData> healthDataList,
                                       AgentPreferenceProfile preferences, AgentMemoryProfile memoryProfile,
                                       List<AgentActionVO> actions) {
        List<String> evidence = new ArrayList<>();
        Map<String, Object> summaryMap = castMap(taskBoard.get("summary"));
        int dailyCompleted = toInt(summaryMap.get("dailyCompleted"));
        int dailyTotal = toInt(summaryMap.get("dailyTotal"));
        int weeklyCompleted = toInt(summaryMap.get("weeklyCompleted"));
        int weeklyTotal = toInt(summaryMap.get("weeklyTotal"));

        evidence.add("\u65e5\u5e38\u4efb\u52a1\u8fdb\u5ea6\uff1a" + dailyCompleted + "/" + dailyTotal);
        evidence.add("\u6bcf\u5468\u6311\u6218\u8fdb\u5ea6\uff1a" + weeklyCompleted + "/" + weeklyTotal);

        if (healthDataList == null || healthDataList.isEmpty()) {
            evidence.add("\u5065\u5eb7\u6863\u6848\uff1a\u6682\u65e0\u8bb0\u5f55");
        } else {
            HealthData latest = healthDataList.get(0);
            StringBuilder builder = new StringBuilder("\u6700\u65b0\u5065\u5eb7\u8bb0\u5f55\uff1a");
            if (latest.getBmi() != null) {
                builder.append(" BMI ").append(latest.getBmi());
            }
            if (latest.getHeartRate() != null) {
                builder.append(" \u5fc3\u7387 ").append(latest.getHeartRate()).append("/min");
            }
            if (latest.getBloodPressure() != null && !latest.getBloodPressure().trim().isEmpty()) {
                builder.append(" \u8840\u538b ").append(latest.getBloodPressure());
            }
            evidence.add(builder.toString());
        }

        if (preferences.hasAnyNote()) {
            evidence.add("\u5df2\u5e94\u7528\u504f\u597d\uff1a" + preferences.describeAppliedPreferences());
        }
        if (memoryProfile.hasHistory()) {
            evidence.add("\u5386\u53f2\u6267\u884c\uff1a\u9a8c\u8bc1\u901a\u8fc7 " + memoryProfile.verifiedCount
                    + "\u6b21\uff0c\u7533\u62a5\u5b8c\u6210 " + memoryProfile.claimedCount
                    + "\u6b21\uff0c\u8df3\u8fc7 " + memoryProfile.skippedCount + "\u6b21");
            if (memoryProfile.usingCarryOverNote()) {
                evidence.add("\u5ef6\u7528\u4e0a\u6b21\u504f\u597d\uff1a" + memoryProfile.lastUserNote);
            }
        }

        if (!actions.isEmpty()) {
            AgentActionVO firstAction = actions.get(0);
            evidence.add("\u7cfb\u7edf\u5f53\u524d\u6700\u4f18\u5148\u63a8\u8fdb\uff1a" + firstAction.getTitle());
        }

        return evidence;
    }

    private AgentActionVO toAgentAction(TaskBoardItemVO item, AgentPreferenceProfile preferences) {
        AgentActionVO action = new AgentActionVO();
        action.setId("task:" + item.getTaskCode());
        action.setTaskCode(item.getTaskCode());
        action.setTitle(item.getTitle());
        action.setReason(buildActionReason(item, preferences));
        action.setPriorityTag("DAILY".equalsIgnoreCase(item.getPeriodType()) ? "\u4eca\u65e5\u4f18\u5148" : "\u672c\u5468\u63a8\u8fdb");
        action.setDurationMinutes(estimateDurationMinutes(item.getTaskCode()));
        action.setEstimatedCarbonSaving(estimateCarbonSaving(item.getTaskCode()));
        action.setEstimatedPoints(item.getRewardPoints() == null ? 0 : item.getRewardPoints());
        action.setActionText(item.getActionText());
        action.setActionPath(item.getActionPath());
        action.setActionType(item.getActionType());
        return action;
    }

    private AgentActionVO createHealthProfileAction(AgentPreferenceProfile preferences) {
        AgentActionVO action = new AgentActionVO();
        action.setId("health:profile");
        action.setTaskCode("HEALTH_PROFILE");
        action.setTitle("\u5148\u8865\u9f50\u4eca\u65e5\u5065\u5eb7\u6863\u6848");
        action.setReason(preferences.healthFirst
                ? "\u4f60\u5f53\u524d\u66f4\u5173\u6ce8\u5065\u5eb7\u4e0e\u8282\u594f\uff0c\u5148\u8865\u9f50\u57fa\u7840\u6570\u636e\uff0cAgent \u540e\u7eed\u624d\u80fd\u7ed9\u51fa\u66f4\u7a33\u7684\u52a8\u4f5c\u6392\u5e8f\u3002"
                : "\u6709\u4e86 BMI\u3001\u5fc3\u7387\u6216\u8840\u538b\u7b49\u57fa\u7840\u6570\u636e\uff0cAgent \u624d\u80fd\u628a\u4f4e\u78b3\u52a8\u4f5c\u548c\u5065\u5eb7\u8282\u594f\u540c\u65f6\u8c03\u5ea6\u3002");
        action.setPriorityTag(preferences.healthFirst ? "\u5065\u5eb7\u4f18\u5148" : "\u8865\u5168\u8d44\u6599");
        action.setDurationMinutes(8);
        action.setEstimatedCarbonSaving(0);
        action.setEstimatedPoints(0);
        action.setActionText("\u53bb\u5f55\u5165");
        action.setActionPath("/pages/healthData/healthData");
        action.setActionType("navigate");
        return action;
    }

    private AgentActionVO createMaintenanceAction(AgentPreferenceProfile preferences) {
        AgentActionVO action = new AgentActionVO();
        action.setId("maintain:walk");
        if (preferences.preferIndoor || preferences.avoidExercise || preferences.energyLow) {
            action.setTaskCode("MAINTAIN_INDOOR");
            action.setTitle("\u7528\u4e00\u4e2a\u5ba4\u5185\u8f7b\u52a8\u4f5c\u4fdd\u4f4f\u4eca\u65e5\u8282\u594f");
            action.setReason("\u4f60\u8fd9\u6b21\u7ed9\u51fa\u7684\u4fe1\u53f7\u66f4\u504f\u5411\u8f7b\u91cf\u3001\u4f4e\u6469\u64e6\u7684\u6267\u884c\u65b9\u5f0f\uff0c\u5148\u4ece\u5ba4\u5185\u53ef\u5b8c\u6210\u7684\u52a8\u4f5c\u5f00\u59cb\u66f4\u5408\u9002\u3002");
            action.setPriorityTag("\u8f7b\u91cf\u7248");
            action.setDurationMinutes(10);
            action.setEstimatedCarbonSaving(40);
            action.setActionText("\u53bb\u5f55\u5165");
            action.setActionPath("/pages/healthData/healthData");
            action.setActionType("navigate");
        } else {
            action.setTaskCode("MAINTAIN_WALK");
            action.setTitle("\u4fdd\u6301\u4e00\u6b21 15 \u5206\u949f\u6b65\u884c");
            action.setReason("\u4eca\u5929\u7684\u57fa\u7840\u76ee\u6807\u5df2\u7ecf\u6bd4\u8f83\u5b8c\u6574\uff0c\u7528\u4f4e\u95e8\u69db\u6b65\u884c\u6765\u7ef4\u6301\u8282\u594f\u6700\u7a33\u3002");
            action.setPriorityTag("\u4fdd\u6301\u72b6\u6001");
            action.setDurationMinutes(15);
            action.setEstimatedCarbonSaving(120);
            action.setActionText("\u53bb\u770b\u6b65\u6570");
            action.setActionPath("/pages/stepCount/stepCount");
            action.setActionType("navigate");
        }
        action.setEstimatedPoints(0);
        return action;
    }

    private int estimateDurationMinutes(String taskCode) {
        if ("DAILY_STEP_6000".equals(taskCode)) {
            return 20;
        }
        if ("DAILY_SPORT_2KM".equals(taskCode)) {
            return 25;
        }
        if ("DAILY_CHECKIN_1".equals(taskCode)) {
            return 5;
        }
        if ("WEEKLY_STEP_50000".equals(taskCode)) {
            return 30;
        }
        if ("WEEKLY_SPORT_3_TIMES".equals(taskCode)) {
            return 35;
        }
        if ("WEEKLY_REDEEM_1".equals(taskCode)) {
            return 10;
        }
        return 15;
    }

    private int estimateCarbonSaving(String taskCode) {
        if ("DAILY_STEP_6000".equals(taskCode)) {
            return 180;
        }
        if ("DAILY_SPORT_2KM".equals(taskCode)) {
            return 130;
        }
        if ("DAILY_CHECKIN_1".equals(taskCode)) {
            return 60;
        }
        if ("WEEKLY_STEP_50000".equals(taskCode)) {
            return 220;
        }
        if ("WEEKLY_SPORT_3_TIMES".equals(taskCode)) {
            return 150;
        }
        return 0;
    }

    private AgentPreferenceProfile parsePreferences(String userNote) {
        AgentPreferenceProfile profile = new AgentPreferenceProfile();
        if (userNote == null || userNote.trim().isEmpty()) {
            return profile;
        }

        String value = userNote.trim().toLowerCase();
        profile.hasNote = true;
        profile.timeSensitive = containsAny(value, "\u8d76", "\u5fd9", "\u6ca1\u65f6\u95f4", "\u4e0a\u8bfe", "deadline", "ddl", "urgent");
        profile.preferEasy = containsAny(value, "\u8f7b\u677e", "\u7b80\u5355", "\u7701\u529b", "\u4f4e\u6469\u64e6", "\u4e0d\u60f3\u592a\u7d2f");
        profile.energyLow = containsAny(value, "\u7d2f", "\u75b2\u60eb", "\u56f0", "\u6ca1\u529b\u6c14", "\u4e0d\u60f3\u52a8");
        profile.preferPoints = containsAny(value, "\u79ef\u5206", "\u5956\u52b1", "\u62ff\u5206", "\u5956\u52b1\u6700\u9ad8");
        profile.preferIndoor = containsAny(value, "\u5ba4\u5185", "\u5bbf\u820d", "\u4e0d\u60f3\u51fa\u53bb", "\u697c\u5185", "\u6559\u5b66\u697c\u91cc");
        profile.wantExercise = containsAny(value, "\u8fd0\u52a8", "\u6d41\u6c57", "\u8bad\u7ec3", "\u60f3\u52a8\u4e00\u4e0b");
        profile.wantSteps = containsAny(value, "\u6b65\u6570", "\u8d70\u8def", "\u6563\u6b65", "\u8d70\u4e00\u8d70");
        profile.avoidExercise = containsAny(value, "\u4e0d\u60f3\u8fd0\u52a8", "\u4e0d\u60f3\u8d70", "\u522b\u8fd0\u52a8", "\u522b\u8d70", "\u80fd\u4e0d\u52a8");
        profile.healthFirst = containsAny(value, "\u5065\u5eb7", "\u8eab\u4f53", "\u5fc3\u7387", "bmi", "\u4f18\u5148\u5065\u5eb7");

        if (profile.preferPoints) {
            profile.focusLabel = "\u79ef\u5206\u4f18\u5148";
        } else if (profile.timeSensitive) {
            profile.focusLabel = "\u8d76\u65f6\u95f4";
        } else if (profile.preferIndoor) {
            profile.focusLabel = "\u5ba4\u5185\u4f18\u5148";
        } else if (profile.healthFirst) {
            profile.focusLabel = "\u5065\u5eb7\u4f18\u5148";
        } else if (profile.wantSteps) {
            profile.focusLabel = "\u8865\u6b65\u6570";
        } else if (profile.wantExercise) {
            profile.focusLabel = "\u60f3\u52a8\u8d77\u6765";
        } else if (profile.preferEasy || profile.energyLow || profile.avoidExercise) {
            profile.focusLabel = "\u8f7b\u91cf\u6267\u884c";
        } else {
            profile.focusLabel = "\u5df2\u91cd\u6392";
        }
        return profile;
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAction(List<AgentActionVO> actions, String id) {
        for (AgentActionVO action : actions) {
            if (action != null && id.equals(action.getId())) {
                return true;
            }
        }
        return false;
    }

    private AgentMemoryProfile loadAgentMemoryProfile(String userId, String activeUserNote) {
        AgentMemoryProfile profile = new AgentMemoryProfile();
        AgentMemory memory = agentMemoryMapper.selectByUsername(userId);
        if (memory == null) {
            return profile;
        }
        profile.verifiedCount = safeInt(memory.getVerifiedCompletionCount());
        profile.claimedCount = safeInt(memory.getClaimedCompletionCount());
        profile.skippedCount = safeInt(memory.getSkippedCount());
        profile.lastUserNote = safeText(memory.getLastUserNote());
        profile.carryOverNote = safeText(activeUserNote).isEmpty() && !profile.lastUserNote.isEmpty();
        profile.preferVerifiedActions = profile.claimedCount > profile.verifiedCount;
        profile.lowFrictionPlan = profile.skippedCount >= 2 && profile.skippedCount >= profile.verifiedCount;
        profile.stretchGoals = profile.verifiedCount >= 3 && profile.skippedCount <= profile.claimedCount;
        return profile;
    }

    private AgentPreferenceProfile buildPlanningPreferences(String userNote, AgentMemoryProfile memoryProfile) {
        return buildPlanningPreferences(userNote, memoryProfile, null, null);
    }

    private AgentPreferenceProfile buildPlanningPreferences(String userNote, AgentMemoryProfile memoryProfile, AgentWeatherVO weather) {
        return buildPlanningPreferences(userNote, memoryProfile, weather, null);
    }

    private AgentPreferenceProfile buildPlanningPreferences(String userNote, AgentMemoryProfile memoryProfile,
                                                            AgentWeatherVO weather, AgentScheduleVO schedule) {
        AgentPreferenceProfile profile = parsePreferences(userNote);
        if (!profile.hasAnyNote() && memoryProfile.usingCarryOverNote()) {
            AgentPreferenceProfile carryOver = parsePreferences(memoryProfile.lastUserNote);
            if (carryOver.hasAnyNote()) {
                applyWeatherContext(carryOver, weather);
                applyScheduleContext(carryOver, schedule);
                return carryOver;
            }
        }
        applyWeatherContext(profile, weather);
        applyScheduleContext(profile, schedule);
        return profile;
    }

    private void applyWeatherContext(AgentPreferenceProfile profile, AgentWeatherVO weather) {
        if (profile == null || weather == null || !"ready".equals(safeText(weather.getStatus()))) {
            return;
        }
        if (Boolean.TRUE.equals(weather.getIndoorPreferred())) {
            profile.weatherIndoorPreferred = true;
            profile.preferIndoor = true;
            if (!profile.hasNote) {
                profile.focusLabel = "\u5929\u6c14\u8c03\u6574";
            }
            return;
        }
        if (Boolean.TRUE.equals(weather.getOutdoorFriendly())) {
            profile.weatherOutdoorFriendly = true;
            if (!profile.hasNote) {
                profile.focusLabel = "\u9002\u5408\u6237\u5916";
            }
        }
    }

    private void applyScheduleContext(AgentPreferenceProfile profile, AgentScheduleVO schedule) {
        if (profile == null || schedule == null || !"ready".equals(safeText(schedule.getStatus()))) {
            return;
        }
        String state = safeText(schedule.getState());
        if ("in_class".equals(state) || "before_class".equals(state)) {
            profile.scheduleTight = true;
            profile.timeSensitive = true;
            profile.preferEasy = true;
            profile.avoidExercise = true;
            if (!profile.hasNote) {
                profile.focusLabel = "\u8bfe\u8868\u7d27\u51d1";
            }
            return;
        }
        if ("short_break".equals(state)) {
            profile.scheduleShortBreak = true;
            profile.timeSensitive = true;
            profile.preferEasy = true;
            if (!profile.hasNote) {
                profile.focusLabel = "\u8bfe\u95f4\u77ed\u4efb\u52a1";
            }
            return;
        }
        if ("long_free".equals(state) || "after_class".equals(state) || "no_course".equals(state)) {
            profile.scheduleLongFree = true;
            if (!profile.hasNote) {
                profile.focusLabel = "\u7a7a\u95f2\u53ef\u8fd0\u52a8";
            }
        }
    }

    private double scoreAction(AgentActionVO action, AgentPreferenceProfile preferences, AgentMemoryProfile memoryProfile) {
        double score = action.getEstimatedPoints() == null ? 0 : action.getEstimatedPoints() * 2D;
        score += action.getEstimatedCarbonSaving() == null ? 0 : action.getEstimatedCarbonSaving() / 12D;

        if (isDailyTask(action.getTaskCode())) {
            score += 80D;
        } else {
            score += 40D;
        }

        if (preferences.timeSensitive) {
            score += shortDurationBonus(action.getDurationMinutes());
        }
        if (preferences.scheduleTight) {
            score += isLowEffortAction(action.getTaskCode()) ? 70D : -45D;
        }
        if (preferences.scheduleShortBreak) {
            score += shortDurationBonus(action.getDurationMinutes());
            score += isWalkingAction(action.getTaskCode()) ? 12D : 0D;
        }
        if (preferences.scheduleLongFree && (preferences.wantExercise || preferences.wantSteps || preferences.weatherOutdoorFriendly)) {
            score += isExerciseAction(action.getTaskCode()) ? 26D : 0D;
        }
        if (preferences.preferEasy || preferences.energyLow) {
            score += isLowEffortAction(action.getTaskCode()) ? 45D : -18D;
        }
        if (preferences.preferIndoor) {
            score += isIndoorFriendlyAction(action.getTaskCode()) ? 48D : -26D;
        }
        if (preferences.weatherIndoorPreferred) {
            score += isIndoorFriendlyAction(action.getTaskCode()) ? 64D : -34D;
        }
        if (preferences.weatherOutdoorFriendly && (preferences.wantExercise || preferences.wantSteps)) {
            score += isExerciseAction(action.getTaskCode()) ? 18D : 0D;
        }
        if (preferences.preferPoints) {
            score += (action.getEstimatedPoints() == null ? 0 : action.getEstimatedPoints()) * 3D;
        }
        if (preferences.wantExercise) {
            score += isExerciseAction(action.getTaskCode()) ? 52D : -8D;
        }
        if (preferences.wantSteps) {
            score += isWalkingAction(action.getTaskCode()) ? 58D : -10D;
        }
        if (preferences.avoidExercise) {
            score += isExerciseAction(action.getTaskCode()) ? -70D : 18D;
        }
        if (preferences.healthFirst) {
            score += isHealthFriendlyAction(action.getTaskCode()) ? 32D : 5D;
        }
        if (memoryProfile.prefersVerifiedActions()) {
            score += requiresClaimCompletion(action.getTaskCode()) ? -42D : 18D;
        }
        if (memoryProfile.needsLowFrictionPlan()) {
            score += isLowEffortAction(action.getTaskCode()) ? 34D : -12D;
        }
        if (memoryProfile.canHandleStretchGoals()) {
            score += isDailyTask(action.getTaskCode()) ? -6D : 16D;
        }
        return score;
    }

    private double shortDurationBonus(Integer durationMinutes) {
        int minutes = durationMinutes == null ? 15 : durationMinutes;
        if (minutes <= 8) {
            return 55D;
        }
        if (minutes <= 15) {
            return 35D;
        }
        if (minutes <= 25) {
            return 12D;
        }
        return -20D;
    }

    private boolean isDailyTask(String taskCode) {
        return taskCode != null && taskCode.startsWith("DAILY_");
    }

    private boolean isWalkingAction(String taskCode) {
        return "DAILY_STEP_6000".equals(taskCode)
                || "WEEKLY_STEP_50000".equals(taskCode)
                || "MAINTAIN_WALK".equals(taskCode);
    }

    private boolean isExerciseAction(String taskCode) {
        return "DAILY_SPORT_2KM".equals(taskCode)
                || "WEEKLY_SPORT_3_TIMES".equals(taskCode)
                || isWalkingAction(taskCode);
    }

    private boolean isIndoorFriendlyAction(String taskCode) {
        return "WEEKLY_REDEEM_1".equals(taskCode)
                || "HEALTH_PROFILE".equals(taskCode)
                || "MAINTAIN_INDOOR".equals(taskCode);
    }

    private boolean isLowEffortAction(String taskCode) {
        return "DAILY_CHECKIN_1".equals(taskCode)
                || "WEEKLY_REDEEM_1".equals(taskCode)
                || "HEALTH_PROFILE".equals(taskCode)
                || "MAINTAIN_INDOOR".equals(taskCode);
    }

    private boolean isHealthFriendlyAction(String taskCode) {
        return "HEALTH_PROFILE".equals(taskCode)
                || "DAILY_STEP_6000".equals(taskCode)
                || "MAINTAIN_WALK".equals(taskCode)
                || "MAINTAIN_INDOOR".equals(taskCode);
    }

    private String buildActionReason(TaskBoardItemVO item, AgentPreferenceProfile preferences) {
        String taskCode = item.getTaskCode();
        if (preferences.scheduleTight && isLowEffortAction(taskCode)) {
            return "\u8bfe\u8868\u663e\u793a\u4f60\u73b0\u5728\u6b63\u5728\u4e0a\u8bfe\u6216\u5373\u5c06\u4e0a\u8bfe\uff0cAgent \u4f18\u5148\u628a\u8fd9\u79cd\u77ed\u3001\u4e0d\u9700\u8fdc\u8ddd\u79bb\u79fb\u52a8\u7684\u52a8\u4f5c\u653e\u5230\u524d\u9762\u3002";
        }
        if (preferences.scheduleTight) {
            return "\u8bfe\u8868\u65f6\u95f4\u8f83\u7d27\uff0cAgent \u4f1a\u964d\u4f4e\u8fdc\u8ddd\u79bb\u8fd0\u52a8\u4f18\u5148\u7ea7\uff0c\u8fd9\u4e00\u6b65\u4f5c\u4e3a\u540e\u7eed\u5019\u9009\u4fdd\u7559\u3002";
        }
        if (preferences.scheduleShortBreak && (isLowEffortAction(taskCode) || isWalkingAction(taskCode))) {
            return "\u4eca\u5929\u8bfe\u95f4\u7a7a\u6863\u6709\u9650\uff0cAgent \u4f18\u5148\u9009\u4e86\u80fd\u5728\u6821\u56ed\u5185\u8f83\u5feb\u5b8c\u6210\u7684\u77ed\u52a8\u4f5c\u3002";
        }
        if (preferences.scheduleLongFree && isExerciseAction(taskCode)) {
            return "\u4eca\u5929\u6709\u8f83\u5b8c\u6574\u7684\u7a7a\u95f2\u65f6\u95f4\uff0cAgent \u624d\u628a\u9700\u8981\u8f83\u591a\u884c\u8d70\u6216\u8fd0\u52a8\u6295\u5165\u7684\u52a8\u4f5c\u5f80\u524d\u63d0\u3002";
        }
        if (preferences.weatherIndoorPreferred && isIndoorFriendlyAction(taskCode)) {
            return "\u5f53\u524d\u5929\u6c14\u66f4\u9002\u5408\u5ba4\u5185\u6216\u77ed\u65f6\u4efb\u52a1\uff0cAgent \u628a\u4e0d\u4f9d\u8d56\u6237\u5916\u8def\u7ebf\u7684\u52a8\u4f5c\u6392\u5728\u524d\u9762\u3002";
        }
        if (preferences.weatherIndoorPreferred && isExerciseAction(taskCode)) {
            return "\u56e0\u4e3a\u5f53\u524d\u5929\u6c14\u5bf9\u6237\u5916\u8fd0\u52a8\u4e0d\u7406\u60f3\uff0cAgent \u4f1a\u5c06\u8fd9\u7c7b\u9ad8\u4f53\u529b\u6216\u6237\u5916\u52a8\u4f5c\u653e\u5230\u66f4\u9760\u540e\u7684\u987a\u4f4d\u3002";
        }
        if (preferences.weatherOutdoorFriendly && isExerciseAction(taskCode)) {
            return "\u5f53\u524d\u5929\u6c14\u9002\u5408\u6237\u5916\u6d3b\u52a8\uff0cAgent \u628a\u80fd\u5e26\u6765\u5b9e\u9645\u6b65\u884c\u6216\u8fd0\u52a8\u91cf\u7684\u52a8\u4f5c\u5f80\u524d\u6392\u3002";
        }
        if (preferences.preferPoints) {
            return "\u4f60\u8fd9\u6b21\u66f4\u60f3\u4f18\u5148\u62ff\u79ef\u5206\uff0c\u8fd9\u6761\u52a8\u4f5c\u5728\u5f53\u524d\u5019\u9009\u91cc\u5956\u52b1\u6bd4\u8f83\u9ad8\uff0c\u5b8c\u6210\u540e\u53cd\u9988\u6700\u76f4\u63a5\u3002";
        }
        if (preferences.timeSensitive) {
            return "\u4f60\u8fd9\u6b21\u63d0\u5230\u65f6\u95f4\u7d27\uff0c\u6240\u4ee5\u6211\u4f18\u5148\u9009\u4e86\u66f4\u77ed\u3001\u66f4\u5bb9\u6613\u63a5\u4e0a\u4f60\u5f53\u524d\u8282\u594f\u7684\u52a8\u4f5c\u3002";
        }
        if ((preferences.preferEasy || preferences.energyLow) && isLowEffortAction(taskCode)) {
            return "\u4f60\u8fd9\u6b21\u66f4\u9002\u5408\u4ece\u8f7b\u91cf\u7248\u52a8\u4f5c\u5f00\u59cb\uff0c\u8fd9\u6761\u57fa\u672c\u6ca1\u6709\u6267\u884c\u538b\u529b\uff0c\u66f4\u5bb9\u6613\u771f\u6b63\u505a\u6389\u3002";
        }
        if (preferences.preferIndoor && isIndoorFriendlyAction(taskCode)) {
            return "\u4f60\u8fd9\u6b21\u66f4\u504f\u5411\u5ba4\u5185\u5b8c\u6210\uff0c\u8fd9\u6761\u4e0d\u9700\u8981\u4f9d\u8d56\u5ba4\u5916\u8def\u7ebf\uff0c\u6267\u884c\u6469\u64e6\u66f4\u4f4e\u3002";
        }
        if (preferences.wantSteps && isWalkingAction(taskCode)) {
            return "\u4f60\u8fd9\u6b21\u60f3\u8865\u8db3\u6b65\u6570\uff0c\u6240\u4ee5\u6211\u628a\u80fd\u76f4\u63a5\u62ac\u9ad8\u6b65\u884c\u8fdb\u5ea6\u7684\u52a8\u4f5c\u9876\u5230\u524d\u9762\u3002";
        }
        if (preferences.wantExercise && isExerciseAction(taskCode)) {
            return "\u4f60\u8fd9\u6b21\u660e\u786e\u60f3\u52a8\u8d77\u6765\uff0c\u6240\u4ee5\u6211\u4f18\u5148\u9009\u4e86\u80fd\u5e26\u6765\u5b9e\u9645\u6d3b\u52a8\u91cf\u7684\u52a8\u4f5c\u3002";
        }
        if (preferences.healthFirst && isHealthFriendlyAction(taskCode)) {
            return "\u4f60\u8fd9\u6b21\u66f4\u91cd\u89c6\u5065\u5eb7\u8282\u594f\uff0c\u8fd9\u6761\u52a8\u4f5c\u5bf9\u8eab\u4f53\u8d1f\u62c5\u66f4\u53ef\u63a7\uff0c\u66f4\u9002\u5408\u5148\u6267\u884c\u3002";
        }
        if (preferences.avoidExercise && !isExerciseAction(taskCode)) {
            return "\u4f60\u8fd9\u6b21\u4e0d\u60f3\u505a\u592a\u591a\u8fd0\u52a8\uff0c\u6240\u4ee5\u6211\u628a\u4e0d\u4f9d\u8d56\u4f53\u529b\u6295\u5165\u7684\u52a8\u4f5c\u5f80\u524d\u63d0\u4e86\u3002";
        }
        return item.getSubtitle();
    }

    private String buildPreferenceTitle(AgentPreferenceProfile preferences) {
        if (preferences.preferPoints) {
            return "\u4eca\u65e5\u6309\u62ff\u5206\u6548\u7387\u91cd\u6392\u8ba1\u5212";
        }
        if (preferences.scheduleTight) {
            return "\u4eca\u65e5\u6309\u8bfe\u8868\u5207\u5230\u77ed\u4efb\u52a1\u8ba1\u5212";
        }
        if (preferences.scheduleShortBreak) {
            return "\u4eca\u65e5\u6309\u8bfe\u95f4\u7a7a\u6863\u6392\u77ed\u52a8\u4f5c";
        }
        if (preferences.scheduleLongFree) {
            return "\u4eca\u65e5\u6709\u7a7a\u95f2\uff0c\u53ef\u5b89\u6392\u5b8c\u6574\u884c\u52a8";
        }
        if (preferences.weatherIndoorPreferred) {
            return "\u4eca\u65e5\u6309\u5929\u6c14\u5207\u5230\u5ba4\u5185\u4f18\u5148\u8ba1\u5212";
        }
        if (preferences.weatherOutdoorFriendly) {
            return "\u4eca\u65e5\u5929\u6c14\u9002\u5408\u6237\u5916\u884c\u52a8";
        }
        if (preferences.timeSensitive) {
            return "\u4eca\u65e5\u5148\u7ed9\u4f60\u4e00\u4e2a\u8d76\u65f6\u95f4\u7248\u8ba1\u5212";
        }
        if (preferences.preferIndoor) {
            return "\u4eca\u65e5\u5148\u7528\u5ba4\u5185\u7248\u8ba1\u5212\u63a5\u4f4f\u4f60";
        }
        if (preferences.wantSteps) {
            return "\u4eca\u65e5\u4f18\u5148\u8865\u6b65\u6570\u8fdb\u5ea6";
        }
        if (preferences.wantExercise) {
            return "\u4eca\u65e5\u4f18\u5148\u6392\u4e00\u4e2a\u80fd\u52a8\u8d77\u6765\u7684\u7248\u672c";
        }
        if (preferences.healthFirst) {
            return "\u4eca\u65e5\u5148\u6309\u5065\u5eb7\u8282\u594f\u6392\u52a8\u4f5c";
        }
        return "\u4eca\u65e5\u5df2\u6309\u4f60\u7684\u60c5\u51b5\u91cd\u6392\u8ba1\u5212";
    }

    private String buildPreferenceReason(AgentPreferenceProfile preferences, List<AgentActionVO> actions) {
        String focus = actions.isEmpty() ? "\u5f53\u524d\u52a8\u4f5c" : actions.get(0).getTitle();
        if (preferences.weatherIndoorPreferred || preferences.weatherOutdoorFriendly || preferences.hasScheduleContext()) {
            return "\u7ed3\u5408\u5f53\u524d\u5929\u6c14\u548c\u8bfe\u8868\u8282\u594f\uff0cAgent \u5df2\u628a\u300c" + preferences.describeAppliedPreferences()
                    + "\u300d\u7eb3\u5165\u6392\u5e8f\uff0c\u73b0\u5728\u6700\u4f18\u5148\u7684\u52a8\u4f5c\u662f\uff1a" + focus + "\u3002";
        }
        return "\u8bfb\u53d6\u5230\u4f60\u7684\u504f\u597d\u540e\uff0cAgent \u5df2\u6539\u7528\u300c" + preferences.describeAppliedPreferences()
                + "\u300d\u8fd9\u5957\u7b56\u7565\u91cd\u6392\u987a\u5e8f\uff0c\u73b0\u5728\u6700\u4f18\u5148\u7684\u52a8\u4f5c\u662f\uff1a" + focus + "\u3002";
    }

    @SuppressWarnings("unchecked")
    private List<TaskBoardItemVO> castTaskItems(Object source) {
        if (source instanceof List) {
            return (List<TaskBoardItemVO>) source;
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object source) {
        if (source instanceof Map) {
            return (Map<String, Object>) source;
        }
        return java.util.Collections.emptyMap();
    }

    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String formatInt(double value) {
        return String.valueOf((int) Math.round(value));
    }

    private String formatDecimal(double value) {
        return String.format("%.2f", value);
    }

    private static class PlannedAction {
        private final AgentActionVO action;
        private final double score;

        private PlannedAction(AgentActionVO action, double score) {
            this.action = action;
            this.score = score;
        }
    }

    private static class CompletionDecision {
        private final boolean accepted;
        private final String mode;
        private final String message;

        private CompletionDecision(boolean accepted, String mode, String message) {
            this.accepted = accepted;
            this.mode = mode;
            this.message = message;
        }
    }

    private static class AgentMemoryProfile {
        private int verifiedCount;
        private int claimedCount;
        private int skippedCount;
        private String lastUserNote = "";
        private boolean carryOverNote;
        private boolean preferVerifiedActions;
        private boolean lowFrictionPlan;
        private boolean stretchGoals;

        private boolean hasHistory() {
            return verifiedCount > 0 || claimedCount > 0 || skippedCount > 0 || !lastUserNote.isEmpty();
        }

        private boolean usingCarryOverNote() {
            return carryOverNote;
        }

        private boolean prefersVerifiedActions() {
            return preferVerifiedActions;
        }

        private boolean needsLowFrictionPlan() {
            return lowFrictionPlan;
        }

        private boolean canHandleStretchGoals() {
            return stretchGoals;
        }
    }

    private static class AgentRuntimeSession {
        private String sessionId;
        private String userId;
        private String userNote = "";
        private String sessionStatus = "ready";
        private String currentActionId = "";
        private Integer planVersion = 1;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<AgentActionVO> actions = new ArrayList<>();
    }

    private static class AgentPreferenceProfile {
        private boolean hasNote;
        private boolean timeSensitive;
        private boolean preferEasy;
        private boolean energyLow;
        private boolean preferPoints;
        private boolean preferIndoor;
        private boolean wantExercise;
        private boolean wantSteps;
        private boolean avoidExercise;
        private boolean healthFirst;
        private boolean weatherIndoorPreferred;
        private boolean weatherOutdoorFriendly;
        private boolean scheduleTight;
        private boolean scheduleShortBreak;
        private boolean scheduleLongFree;
        private String focusLabel = "\u5df2\u91cd\u6392";

        private boolean hasAnyNote() {
            return hasNote;
        }

        private boolean hasStrongPreference() {
            return timeSensitive
                    || preferEasy
                    || energyLow
                    || preferPoints
                    || preferIndoor
                    || wantExercise
                    || wantSteps
                    || avoidExercise
                    || healthFirst
                    || weatherIndoorPreferred
                    || weatherOutdoorFriendly
                    || scheduleTight
                    || scheduleShortBreak
                    || scheduleLongFree;
        }

        private boolean hasScheduleContext() {
            return scheduleTight || scheduleShortBreak || scheduleLongFree;
        }

        private String describeAppliedPreferences() {
            List<String> labels = new ArrayList<>();
            if (timeSensitive) {
                labels.add("\u8d76\u65f6\u95f4");
            }
            if (preferEasy || energyLow) {
                labels.add("\u8f7b\u91cf\u6267\u884c");
            }
            if (preferPoints) {
                labels.add("\u79ef\u5206\u4f18\u5148");
            }
            if (preferIndoor) {
                labels.add("\u5ba4\u5185\u4f18\u5148");
            }
            if (wantExercise) {
                labels.add("\u60f3\u52a8\u8d77\u6765");
            }
            if (wantSteps) {
                labels.add("\u8865\u6b65\u6570");
            }
            if (avoidExercise) {
                labels.add("\u907f\u5f00\u9ad8\u4f53\u529b");
            }
            if (healthFirst) {
                labels.add("\u5065\u5eb7\u4f18\u5148");
            }
            if (weatherIndoorPreferred) {
                labels.add("\u5929\u6c14\u5ba4\u5185\u4f18\u5148");
            }
            if (weatherOutdoorFriendly) {
                labels.add("\u5929\u6c14\u9002\u5408\u6237\u5916");
            }
            if (scheduleTight) {
                labels.add("\u8bfe\u8868\u7d27\u51d1");
            }
            if (scheduleShortBreak) {
                labels.add("\u8bfe\u95f4\u77ed\u4efb\u52a1");
            }
            if (scheduleLongFree) {
                labels.add("\u7a7a\u95f2\u53ef\u8fd0\u52a8");
            }
            if (labels.isEmpty()) {
                return "\u9ed8\u8ba4\u7b56\u7565";
            }
            return String.join(" / ", labels);
        }
    }
}
