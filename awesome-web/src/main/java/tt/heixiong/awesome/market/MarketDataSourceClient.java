package tt.heixiong.awesome.market;

import java.util.List;

public interface MarketDataSourceClient {

    boolean supports(String source);

    List<MarketDataCandle> fetchCandles(String symbol, String interval, int limit);
}
