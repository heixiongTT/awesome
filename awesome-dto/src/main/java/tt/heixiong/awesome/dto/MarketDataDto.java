package tt.heixiong.awesome.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class MarketDataDto {

    private Long id;
    private String source;
    private String symbol;
    private String marketInterval;
    private Instant openTime;
    private Instant closeTime;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal volume;
    private Long tradeCount;
}
