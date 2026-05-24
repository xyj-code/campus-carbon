package com.campus.carbon.service.impl;

import com.campus.carbon.model.dto.AgentSportPlanVO;
import com.campus.carbon.model.dto.AgentRoutePointVO;
import com.campus.carbon.service.MapVenueService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MapVenueServiceImpl implements MapVenueService {

    private static final String SEARCH_ENDPOINT = "https://restapi.amap.com/v3/place/around";
    private static final String WALKING_ENDPOINT = "https://restapi.amap.com/v3/direction/walking";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${carbon.amap.web-key:}")
    private String amapWebKey;

    @Value("${carbon.amap.search-radius-meters:3000}")
    private Integer searchRadiusMeters;

    @Override
    public AgentSportPlanVO buildSportPlan(String userNote, Double latitude, Double longitude) {
        SportIntent intent = parseIntent(userNote);
        if (intent == null) {
            return null;
        }

        if (safeText(amapWebKey).isEmpty()) {
            AgentSportPlanVO plan = createBasePlan(intent);
            plan.setStatus("config_missing");
            plan.setRecommendationReason("\u5c1a\u672a\u914d\u7f6e\u9ad8\u5fb7 Web API Key\uff0c\u6682\u65f6\u65e0\u6cd5\u641c\u7d22\u9644\u8fd1\u573a\u5730");
            return plan;
        }

        if (latitude == null || longitude == null) {
            AgentSportPlanVO plan = createBasePlan(intent);
            plan.setStatus("location_required");
            plan.setRecommendationReason("\u9700\u8981\u4f60\u7684\u5f53\u524d\u4f4d\u7f6e\u540e\uff0cAgent \u624d\u80fd\u5e2e\u4f60\u641c\u7d22\u9644\u8fd1\u5408\u9002\u7684\u573a\u5730");
            return plan;
        }

        try {
            VenueSearchResult venueSearchResult = searchNearestVenue(intent, latitude, longitude);
            if (venueSearchResult.errorMessage != null && !venueSearchResult.errorMessage.isEmpty()) {
                AgentSportPlanVO plan = createBasePlan(intent);
                if (venueSearchResult.queryText != null) {
                    plan.setQueryText(venueSearchResult.queryText);
                }
                plan.setStatus("request_failed");
                plan.setRecommendationReason(venueSearchResult.errorMessage);
                return plan;
            }

            VenueCandidate venue = venueSearchResult.venue;
            if (venue == null) {
                AgentSportPlanVO plan = createBasePlan(intent);
                if (venueSearchResult.queryText != null) {
                    plan.setQueryText(venueSearchResult.queryText);
                }
                plan.setStatus("no_result");
                plan.setRecommendationReason("\u9644\u8fd1\u6682\u672a\u627e\u5230\u5408\u9002\u7684" + intent.label + "\u573a\u5730\uff0c\u53ef\u4ee5\u6362\u4e2a\u5173\u952e\u8bcd\u518d\u8bd5");
                return plan;
            }

            AgentSportPlanVO plan = createBasePlan(intent);
            if (venueSearchResult.queryText != null) {
                plan.setQueryText(venueSearchResult.queryText);
            }
            plan.setStatus("ready");
            plan.setVenueName(venue.name);
            plan.setVenueAddress(venue.address);
            plan.setVenueType(venue.type);
            plan.setVenueDistanceMeters(venue.distanceMeters);
            plan.setOriginLatitude(latitude);
            plan.setOriginLongitude(longitude);
            plan.setDestinationLatitude(venue.latitude);
            plan.setDestinationLongitude(venue.longitude);

            RoutePlan routePlan = buildWalkingRoute(latitude, longitude, venue.latitude, venue.longitude);
            if (routePlan != null) {
                plan.setTransportMode("\u6b65\u884c");
                plan.setRouteDistanceMeters(routePlan.distanceMeters);
                plan.setRouteDurationMinutes(routePlan.durationMinutes);
                plan.setRouteSteps(routePlan.steps);
                plan.setRoutePolyline(routePlan.polyline);
            }

            String routeSummary = routePlan == null
                    ? "\u5df2\u4e3a\u4f60\u627e\u5230\u6700\u8fd1\u7684" + intent.label + "\u573a\u5730"
                    : "\u5df2\u4e3a\u4f60\u627e\u5230\u9644\u8fd1\u9002\u5408\u7684" + intent.label + "\u573a\u5730\uff0c\u6b65\u884c\u7ea6 " + routePlan.durationMinutes + " \u5206\u949f\u53ef\u5230";
            plan.setRecommendationReason(routeSummary);
            return plan;
        } catch (IOException e) {
            AgentSportPlanVO plan = createBasePlan(intent);
            plan.setStatus("request_failed");
            plan.setRecommendationReason("\u573a\u5730\u641c\u7d22\u6216\u8def\u7ebf\u89c4\u5212\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
            return plan;
        }
    }

    private AgentSportPlanVO createBasePlan(SportIntent intent) {
        AgentSportPlanVO plan = new AgentSportPlanVO();
        plan.setSportType(intent.sportType);
        plan.setSportLabel(intent.label);
        plan.setQueryText(intent.queryHints.isEmpty() ? "" : intent.queryHints.get(0));
        plan.setTransportMode("\u6b65\u884c");
        return plan;
    }

    private VenueSearchResult searchNearestVenue(SportIntent intent, Double latitude, Double longitude) throws IOException {
        VenueSearchResult fallback = new VenueSearchResult();
        for (String queryHint : intent.queryHints) {
            for (Integer radius : buildRadiusCandidates()) {
                VenueSearchResult result = searchNearestVenueOnce(queryHint, radius, latitude, longitude);
                if (result.errorMessage != null && !result.errorMessage.isEmpty()) {
                    return result;
                }
                if (result.venue != null) {
                    result.queryText = queryHint;
                    return result;
                }
                fallback.queryText = queryHint;
            }
        }
        return fallback;
    }

    private VenueSearchResult searchNearestVenueOnce(String queryHint, Integer radius, Double latitude, Double longitude) throws IOException {
        HttpUrl.Builder builder = HttpUrl.parse(SEARCH_ENDPOINT).newBuilder()
                .addQueryParameter("key", amapWebKey)
                .addQueryParameter("location", formatLocation(longitude, latitude))
                .addQueryParameter("keywords", queryHint)
                .addQueryParameter("radius", String.valueOf(radius))
                .addQueryParameter("offset", "20")
                .addQueryParameter("page", "1")
                .addQueryParameter("extensions", "base")
                .addQueryParameter("sortrule", "distance");

        Request request = new Request.Builder().url(builder.build()).get().build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            String text = body == null ? "" : body.string();
            VenueSearchResult result = new VenueSearchResult();
            if (!response.isSuccessful()) {
                result.errorMessage = "\u9ad8\u5fb7\u573a\u5730\u641c\u7d22\u8bf7\u6c42\u5931\u8d25\uff0cHTTP " + response.code();
                return result;
            }
            JsonNode root = mapper.readTree(text);
            String status = safeText(root.path("status").asText(""));
            if (!"1".equals(status)) {
                result.errorMessage = buildAmapError("\u573a\u5730\u641c\u7d22", root);
                return result;
            }
            JsonNode pois = root.path("pois");
            if (!pois.isArray()) {
                return result;
            }
            for (JsonNode poi : pois) {
                VenueCandidate venue = toVenueCandidate(poi);
                if (venue != null) {
                    result.venue = venue;
                    return result;
                }
            }
            return result;
        }
    }

    private RoutePlan buildWalkingRoute(Double latitude, Double longitude, Double destinationLatitude, Double destinationLongitude) throws IOException {
        HttpUrl.Builder builder = HttpUrl.parse(WALKING_ENDPOINT).newBuilder()
                .addQueryParameter("key", amapWebKey)
                .addQueryParameter("origin", formatLocation(longitude, latitude))
                .addQueryParameter("destination", formatLocation(destinationLongitude, destinationLatitude));

        Request request = new Request.Builder().url(builder.build()).get().build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            String text = body == null ? "" : body.string();
            if (!response.isSuccessful()) {
                return null;
            }
            JsonNode root = mapper.readTree(text);
            String status = safeText(root.path("status").asText(""));
            if (!"1".equals(status)) {
                return null;
            }
            JsonNode paths = root.path("route").path("paths");
            if (!paths.isArray() || paths.isEmpty()) {
                return null;
            }
            JsonNode firstPath = paths.get(0);
            RoutePlan routePlan = new RoutePlan();
            routePlan.distanceMeters = parseInteger(firstPath.path("distance").asText("0"));
            routePlan.durationMinutes = Math.max(1, (int) Math.ceil(parseInteger(firstPath.path("duration").asText("0")) / 60D));
            routePlan.steps = new ArrayList<>();
            routePlan.polyline = new ArrayList<>();
            JsonNode steps = firstPath.path("steps");
            if (steps.isArray()) {
                int count = 0;
                for (JsonNode step : steps) {
                    addPolyline(routePlan.polyline, step.path("polyline").asText(""));
                    String instruction = safeText(step.path("instruction").asText(""));
                    if (!instruction.isEmpty() && count < 4) {
                        routePlan.steps.add(instruction);
                        count++;
                    }
                }
            }
            return routePlan;
        }
    }

    private VenueCandidate toVenueCandidate(JsonNode poi) {
        String location = safeText(poi.path("location").asText(""));
        if (location.isEmpty() || !location.contains(",")) {
            return null;
        }
        String[] parts = location.split(",");
        if (parts.length != 2) {
            return null;
        }
        try {
            VenueCandidate venue = new VenueCandidate();
            venue.longitude = Double.parseDouble(parts[0]);
            venue.latitude = Double.parseDouble(parts[1]);
            venue.name = safeText(poi.path("name").asText(""));
            venue.address = safeText(poi.path("address").asText(""));
            venue.type = safeText(poi.path("type").asText(""));
            venue.distanceMeters = parseInteger(poi.path("distance").asText("0"));
            return venue;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private SportIntent parseIntent(String userNote) {
        String text = safeText(userNote).toLowerCase(Locale.ROOT);
        if (text.isEmpty()) {
            return null;
        }

        Map<String, SportIntent> dictionary = buildIntentDictionary();
        for (Map.Entry<String, SportIntent> entry : dictionary.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Map<String, SportIntent> buildIntentDictionary() {
        Map<String, SportIntent> dictionary = new LinkedHashMap<>();
        registerIntent(dictionary, new SportIntent("running", "\u8dd1\u6b65", "\u8dd1\u9053", "\u64cd\u573a", "\u516c\u56ed"), "\u8dd1\u6b65", "\u591c\u8dd1", "\u6162\u8dd1", "\u6668\u8dd1", "running", "jogging");
        registerIntent(dictionary, new SportIntent("walking", "\u6563\u6b65", "\u516c\u56ed", "\u7eff\u9053", "\u5e7f\u573a"), "\u6563\u6b65", "\u8d70\u8d70", "\u905b\u516c\u56ed", "walking");
        registerIntent(dictionary, new SportIntent("basketball", "\u7bee\u7403", "\u7bee\u7403\u573a", "\u4f53\u80b2\u9986"), "\u7bee\u7403", "\u6253\u7403", "basketball");
        registerIntent(dictionary, new SportIntent("badminton", "\u7fbd\u6bdb\u7403", "\u7fbd\u6bdb\u7403\u9986", "\u4f53\u80b2\u9986"), "\u7fbd\u6bdb\u7403", "badminton");
        registerIntent(dictionary, new SportIntent("football", "\u8db3\u7403", "\u8db3\u7403\u573a", "\u64cd\u573a"), "\u8db3\u7403", "\u8e22\u7403", "football", "soccer");
        registerIntent(dictionary, new SportIntent("swimming", "\u6e38\u6cf3", "\u6e38\u6cf3\u9986", "\u6e38\u6cf3\u6c60"), "\u6e38\u6cf3", "swimming");
        registerIntent(dictionary, new SportIntent("tennis", "\u7f51\u7403", "\u7f51\u7403\u573a", "\u4f53\u80b2\u9986"), "\u7f51\u7403", "tennis");
        registerIntent(dictionary, new SportIntent("table_tennis", "\u4e52\u4e53\u7403", "\u4e52\u4e53\u7403\u9986", "\u4f53\u80b2\u9986"), "\u4e52\u4e53\u7403", "\u6253\u4e52\u4e53", "table tennis", "ping pong");
        registerIntent(dictionary, new SportIntent("fitness", "\u5065\u8eab", "\u5065\u8eab\u623f", "\u4f53\u80b2\u9986"), "\u5065\u8eab", "\u5065\u8eab\u623f", "\u5668\u68b0", "fitness", "gym");
        registerIntent(dictionary, new SportIntent("cycling", "\u9a91\u884c", "\u7eff\u9053", "\u516c\u56ed", "\u6ee8\u6c5f"), "\u9a91\u884c", "\u9a91\u8f66", "\u81ea\u884c\u8f66", "cycling", "biking");
        return dictionary;
    }

    private List<Integer> buildRadiusCandidates() {
        int baseRadius = searchRadiusMeters == null || searchRadiusMeters <= 0 ? 3000 : searchRadiusMeters;
        List<Integer> radiusCandidates = new ArrayList<>();
        radiusCandidates.add(baseRadius);
        int extendedRadius = Math.max(baseRadius * 2, 5000);
        if (extendedRadius != baseRadius) {
            radiusCandidates.add(extendedRadius);
        }
        return radiusCandidates;
    }

    private String buildAmapError(String actionName, JsonNode root) {
        String info = safeText(root.path("info").asText(""));
        String infocode = safeText(root.path("infocode").asText(""));
        if (info.isEmpty() && infocode.isEmpty()) {
            return "\u9ad8\u5fb7" + actionName + "\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5";
        }
        return "\u9ad8\u5fb7" + actionName + "\u5931\u8d25\uff1a" + info + (infocode.isEmpty() ? "" : " (" + infocode + ")");
    }

    private void addPolyline(List<AgentRoutePointVO> routePolyline, String polylineText) {
        String text = safeText(polylineText);
        if (text.isEmpty()) {
            return;
        }
        String[] points = text.split(";");
        for (String point : points) {
            String[] parts = point.split(",");
            if (parts.length != 2) {
                continue;
            }
            try {
                Double longitude = Double.parseDouble(parts[0]);
                Double latitude = Double.parseDouble(parts[1]);
                if (routePolyline.isEmpty()) {
                    routePolyline.add(new AgentRoutePointVO(latitude, longitude));
                    continue;
                }
                AgentRoutePointVO last = routePolyline.get(routePolyline.size() - 1);
                if (!latitude.equals(last.getLatitude()) || !longitude.equals(last.getLongitude())) {
                    routePolyline.add(new AgentRoutePointVO(latitude, longitude));
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void registerIntent(Map<String, SportIntent> dictionary, SportIntent intent, String... aliases) {
        List<String> values = Arrays.asList(aliases);
        for (String alias : values) {
            dictionary.put(alias, intent);
        }
    }

    private String formatLocation(Double longitude, Double latitude) {
        return String.format(Locale.US, "%.6f,%.6f", longitude, latitude);
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private static class SportIntent {
        private final String sportType;
        private final String label;
        private final List<String> queryHints;

        private SportIntent(String sportType, String label, String... queryHints) {
            this.sportType = sportType;
            this.label = label;
            if (queryHints == null || queryHints.length == 0) {
                this.queryHints = Collections.emptyList();
            } else {
                this.queryHints = Arrays.asList(queryHints);
            }
        }
    }

    private static class VenueSearchResult {
        private VenueCandidate venue;
        private String queryText;
        private String errorMessage;
    }

    private static class VenueCandidate {
        private String name;
        private String address;
        private String type;
        private Integer distanceMeters;
        private Double latitude;
        private Double longitude;
    }

    private static class RoutePlan {
        private Integer distanceMeters;
        private Integer durationMinutes;
        private List<String> steps = new ArrayList<>();
        private List<AgentRoutePointVO> polyline = new ArrayList<>();
    }
}
