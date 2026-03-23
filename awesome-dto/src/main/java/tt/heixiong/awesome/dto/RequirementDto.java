package tt.heixiong.awesome.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequirementDto {

    private Long id;

    private String title;

    private String description;

    private String priority;

    private String status;

    private String creator;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
