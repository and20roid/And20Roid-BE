package com.and20roid.backend.vo;

import com.and20roid.backend.entity.ParticipationStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadParticipantsResponse {

    private List<ReadParticipantResponse> readParticipantResponses;

    public ReadParticipantsResponse(List<ParticipationStatus> participationStatuses) {
        this.readParticipantResponses = participationStatuses.stream()
                .map(ReadParticipantResponse::new)
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    static class ReadParticipantResponse {
        private Long userId;
        private String email;

        public ReadParticipantResponse(ParticipationStatus participationStatus) {
            this.userId = participationStatus.getUser().getId();
            this.email = participationStatus.getEmail();
        }
    }
}
