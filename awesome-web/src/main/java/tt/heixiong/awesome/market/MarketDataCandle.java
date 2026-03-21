package tt.heixiong.awesome.market;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class MarketDataCandle {

    private final String source;
    private final String symbol;
    private final String marketInterval;
    private final Instant openTime;
    private final Instant closeTime;
    private final BigDecimal openPrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal closePrice;
    private final BigDecimal volume;
    private final Long tradeCount;
}
