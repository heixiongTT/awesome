package tt.heixiong.awesome.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import tt.heixiong.awesome.domain.MarketDataRecord;
import tt.heixiong.awesome.dto.MarketDataIngestionResultDto;
import tt.heixiong.awesome.exception.BusinessException;
import tt.heixiong.awesome.market.MarketDataCandle;
import tt.heixiong.awesome.market.MarketDataSourceClient;
import tt.heixiong.awesome.repository.MarketDataRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarketDataServiceImplTest {

    @Mock
    private MarketDataSourceClient marketDataSourceClient;

    @Mock
    private MarketDataRepository marketDataRepository;

    @InjectMocks
    private MarketDataServiceImpl marketDataService;

    @Before
    public void setUp() {
        marketDataService = new MarketDataServiceImpl(Collections.singletonList(marketDataSourceClient), marketDataRepository);
    }

    @Test
    public void ingestStoresOnlyNewCandles() {
        MarketDataCandle first = candle(Instant.parse("2026-03-20T00:00:00Z"));
        MarketDataCandle second = candle(Instant.parse("2026-03-20T00:01:00Z"));
        when(marketDataSourceClient.supports("BINANCE")).thenReturn(true);
        when(marketDataSourceClient.fetchCandles("BTCUSDT", "1m", 2)).thenReturn(Arrays.asList(first, second));
        when(marketDataRepository.existsBySourceAndSymbolAndMarketIntervalAndOpenTime(
                "BINANCE", "BTCUSDT", "1m", first.getOpenTime())).thenReturn(false);
        when(marketDataRepository.existsBySourceAndSymbolAndMarketIntervalAndOpenTime(
                "BINANCE", "BTCUSDT", "1m", second.getOpenTime())).thenReturn(true);

        MarketDataIngestionResultDto result = marketDataService.ingest("binance", "btcusdt", "1m", 2);

        assertEquals(2, result.getFetched());
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getDuplicates());
        assertEquals(Instant.parse("2026-03-20T00:00:00Z"), result.getEarliestOpenTime());
        assertEquals(Instant.parse("2026-03-20T00:01:00Z"), result.getLatestOpenTime());

        ArgumentCaptor<MarketDataRecord> captor = ArgumentCaptor.forClass(MarketDataRecord.class);
        verify(marketDataRepository).save(captor.capture());
        assertEquals("BTCUSDT", captor.getValue().getSymbol());
        assertNotNull(captor.getValue().getClosePrice());
    }

    @Test
    public void ingestRejectsUnsupportedSource() {
        when(marketDataSourceClient.supports("UNKNOWN")).thenReturn(false);

        try {
            marketDataService.ingest("UNKNOWN", "BTCUSDT", "1m", 10);
            fail("Expected BusinessException");
        } catch (BusinessException ex) {
            assertEquals("UNSUPPORTED_MARKET_DATA_SOURCE", ex.getCode());
        }

        verify(marketDataRepository, never()).save(any(MarketDataRecord.class));
    }

    @Test
    public void listReturnsRequestedSubset() {
        MarketDataRecord first = new MarketDataRecord();
        first.setId(2L);
        MarketDataRecord second = new MarketDataRecord();
        second.setId(1L);
        when(marketDataRepository.findTop200BySourceAndSymbolAndMarketIntervalOrderByOpenTimeDesc(
                "BINANCE", "BTCUSDT", "1m")).thenReturn(Arrays.asList(first, second));

        assertEquals(1, marketDataService.list("BINANCE", "BTCUSDT", "1m", 1).size());
        verify(marketDataRepository).findTop200BySourceAndSymbolAndMarketIntervalOrderByOpenTimeDesc(
                eq("BINANCE"), eq("BTCUSDT"), eq("1m"));
    }

    private MarketDataCandle candle(Instant openTime) {
        return MarketDataCandle.builder()
                .source("BINANCE")
                .symbol("BTCUSDT")
                .marketInterval("1m")
                .openTime(openTime)
                .closeTime(openTime.plusSeconds(59))
                .openPrice(new BigDecimal("100.10"))
                .highPrice(new BigDecimal("101.20"))
                .lowPrice(new BigDecimal("99.90"))
                .closePrice(new BigDecimal("100.80"))
                .volume(new BigDecimal("12.34"))
                .tradeCount(123L)
                .build();
    }
}
