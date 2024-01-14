package com.and20roid.backend.vo;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadRankingResponse {
    private List<RankInfo> rankInfos;

    public ReadRankingResponse(List<ReadRankQuery> readRankQueries) {
        this.rankInfos = readRankQueries.stream()
                .map(readRankQuery -> new RankInfo(readRankQuery.getUserId(),
                        readRankQuery.getRank(), readRankQuery.getNickname(),
                        readRankQuery.getCompletedTestCount()))
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class RankInfo{
        private long userId;
        private int rank;
        private String nickname;
        private int completedTestCount;
    }
}
