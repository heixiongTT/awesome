package tt.heixiong.awesome.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tt.heixiong.awesome.domain.MarketDataRecord;
import tt.heixiong.awesome.dto.MarketDataIngestionResultDto;
import tt.heixiong.awesome.service.MarketDataService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = MarketDataCtrl.class, properties = {"server.servlet.context-path=", "eureka.client.enabled=false", "spring.cloud.discovery.enabled=false"})
public class MarketDataCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarketDataService marketDataService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    public void ingestRejectsBlankSymbol() throws Exception {
        mockMvc.perform(post("/market-data/ingestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"source\":\"BINANCE\",\"symbol\":\"\",\"interval\":\"1m\",\"limit\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    public void ingestReturnsSummary() throws Exception {
        MarketDataIngestionResultDto resultDto = new MarketDataIngestionResultDto();
        resultDto.setSource("BINANCE");
        resultDto.setSymbol("BTCUSDT");
        resultDto.setMarketInterval("1m");
        resultDto.setInserted(3);
        resultDto.setDuplicates(1);
        when(marketDataService.ingest(eq("BINANCE"), eq("BTCUSDT"), eq("1m"), eq(4))).thenReturn(resultDto);

        mockMvc.perform(post("/market-data/ingestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"source\":\"BINANCE\",\"symbol\":\"BTCUSDT\",\"interval\":\"1m\",\"limit\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.inserted").value(3))
                .andExpect(jsonPath("$.data.duplicates").value(1));
    }

    @Test
    public void listReturnsStoredRecords() throws Exception {
        MarketDataRecord record = new MarketDataRecord();
        record.setId(1L);
        record.setSource("BINANCE");
        record.setSymbol("BTCUSDT");
        record.setMarketInterval("1m");
        record.setOpenTime(Instant.parse("2026-03-20T00:00:00Z"));
        record.setCloseTime(Instant.parse("2026-03-20T00:00:59Z"));
        record.setOpenPrice(new BigDecimal("100.1"));
        record.setHighPrice(new BigDecimal("101.1"));
        record.setLowPrice(new BigDecimal("99.1"));
        record.setClosePrice(new BigDecimal("100.9"));
        record.setVolume(new BigDecimal("2.5"));
        record.setTradeCount(15L);
        when(marketDataService.list("BINANCE", "BTCUSDT", "1m", 10)).thenReturn(Collections.singletonList(record));

        mockMvc.perform(get("/market-data")
                        .param("source", "BINANCE")
                        .param("symbol", "BTCUSDT")
                        .param("interval", "1m")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].symbol").value("BTCUSDT"))
                .andExpect(jsonPath("$.data[0].tradeCount").value(15));
    }
}
