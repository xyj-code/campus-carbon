package com.campus.carbon.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RankResponse {
    private int code;
    private String message;
    private RankData data;

    public RankResponse() {
        this.code = 200;
        this.message = "success";
    }

    @Data
    public static class RankData {
        private MyRank myRank;
        private List<RankItem> rankList;
    }

    @Data
    public static class MyRank {
        private int rank;
        private double carbonReduction;
        private int points;
    }

    @Data
    public static class RankItem {
        private String id;
        private String username;
        private String name;
        private String avatar;
        private double carbonReduction;
        private int points;
        private int likes;
        @JsonProperty("isLiked")
        private boolean isLiked;
        @JsonProperty("isSelf")
        private boolean isSelf;
    }
}
