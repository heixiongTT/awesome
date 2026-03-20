package tt.heixiong.awesome.market;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tt.heixiong.awesome.exception.RemoteCallException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class BinanceMarketDataSourceClient implements MarketDataSourceClient {

    private static final String SOURCE = "BINANCE";
    private static final String URL_TEMPLATE = "https://api.binance.com/api/v3/klines?symbol={symbol}&interval={interval}&limit={limit}";

    private final RestTemplate restTemplate;

    public BinanceMarketDataSourceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public boolean supports(String source) {
        return SOURCE.equalsIgnoreCase(source);
    }

    @Override
    public List<MarketDataCandle> fetchCandles(String symbol, String interval, int limit) {
        try {
            ResponseEntity<List> responseEntity = restTemplate.getForEntity(URL_TEMPLATE, List.class, symbol, interval, limit);
            List<List> response = responseEntity.getBody();
            List<MarketDataCandle> candles = new ArrayList<MarketDataCandle>();
            if (response == null) {
                return candles;
            }
            for (List item : response) {
                candles.add(MarketDataCandle.builder()
                        .source(SOURCE)
                        .symbol(symbol.toUpperCase())
                        .marketInterval(interval.toLowerCase())
                        .openTime(Instant.ofEpochMilli(Long.parseLong(String.valueOf(item.get(0)))))
                        .openPrice(new BigDecimal(String.valueOf(item.get(1))))
                        .highPrice(new BigDecimal(String.valueOf(item.get(2))))
                        .lowPrice(new BigDecimal(String.valueOf(item.get(3))))
                        .closePrice(new BigDecimal(String.valueOf(item.get(4))))
                        .volume(new BigDecimal(String.valueOf(item.get(5))))
                        .closeTime(Instant.ofEpochMilli(Long.parseLong(String.valueOf(item.get(6)))))
                        .tradeCount(Long.parseLong(String.valueOf(item.get(8))))
                        .build());
            }
            return candles;
        } catch (Exception ex) {
            throw new RemoteCallException("Failed to fetch Binance market data", ex);
        }
    }
}
