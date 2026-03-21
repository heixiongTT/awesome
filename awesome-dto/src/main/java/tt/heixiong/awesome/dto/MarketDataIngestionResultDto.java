package tt.heixiong.awesome.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MarketDataIngestionResultDto {

    private String source;
    private String symbol;
    private String marketInterval;
    private int requested;
    private int fetched;
    private int inserted;
    private int duplicates;
    private Instant earliestOpenTime;
    private Instant latestOpenTime;
}
