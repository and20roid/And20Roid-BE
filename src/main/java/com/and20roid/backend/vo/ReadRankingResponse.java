package com.and20roid.backend.vo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadRankingResponse {
    private List<RankInfo> rankInfos;

    public ReadRankingResponse(List<ReadRankQuery> readRankQueries, Map<Long, Integer> interactionCountAsTesterMap, Map<Long, Integer> interactionCountAsUploaderMap) {
        this.rankInfos = readRankQueries.stream()
                .map(readRankQuery -> new RankInfo(readRankQuery.getUserId(),
                        readRankQuery.getRank(), readRankQuery.getNickname(),
                        readRankQuery.getCompletedTestCount(), interactionCountAsTesterMap, interactionCountAsUploaderMap))
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    static class RankInfo{
        private long userId;
        private int rank;
        private String nickname;
        private int completedTestCount;
        private int interactionCountAsTester; // 본인이 참가
        private int interactionCountAsUploader; // 본인이 게시
        private boolean isRelatedUser;

        public RankInfo(long userId, int rank, String nickname, int completedTestCount, Map<Long, Integer> interactionCountAsTesterMap, Map<Long, Integer> interactionCountAsUploaderMap) {
            int interactionCountAsTester = interactionCountAsTesterMap.getOrDefault(userId, 0);
            int interactionCountAsUploader = interactionCountAsUploaderMap.getOrDefault(userId, 0);

            this.userId = userId;
            this.rank = rank;
            this.nickname = nickname;
            this.completedTestCount = completedTestCount;
            this.interactionCountAsTester = interactionCountAsTester;
            this.interactionCountAsUploader = interactionCountAsUploader;
            this.isRelatedUser = !((interactionCountAsTester == 0) && (interactionCountAsUploader == 0));
        }
    }
}
