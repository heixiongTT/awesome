package tt.heixiong.awesome.web;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.contract.MarketDataContract;
import tt.heixiong.awesome.domain.MarketDataRecord;
import tt.heixiong.awesome.dto.MarketDataDto;
import tt.heixiong.awesome.dto.MarketDataIngestionResultDto;
import tt.heixiong.awesome.req.MarketDataIngestionReq;
import tt.heixiong.awesome.service.MarketDataService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Market Data Controller")
@RestController
@ResponseBody
@RequestMapping("/market-data")
public class MarketDataCtrl implements MarketDataContract {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final MarketDataService marketDataService;
    private final HttpServletRequest request;

    public MarketDataCtrl(MarketDataService marketDataService, HttpServletRequest request) {
        this.marketDataService = marketDataService;
        this.request = request;
    }

    @Override
    public ApiResponse<MarketDataIngestionResultDto> ingestMarketData(@Valid @RequestBody MarketDataIngestionReq req) {
        return success(marketDataService.ingest(req.getSource(), req.getSymbol(), req.getInterval(), req.getLimit()));
    }

    @Override
    public ApiResponse<List<MarketDataDto>> listMarketData(@RequestParam("source") String source,
                                                           @RequestParam("symbol") String symbol,
                                                           @RequestParam("interval") String interval,
                                                           @RequestParam(value = "limit", required = false) Integer limit) {
        List<MarketDataDto> dtos = marketDataService.list(source, symbol, interval, limit)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return success(dtos);
    }

    private MarketDataDto toDto(MarketDataRecord record) {
        MarketDataDto dto = new MarketDataDto();
        dto.setId(record.getId());
        dto.setSource(record.getSource());
        dto.setSymbol(record.getSymbol());
        dto.setMarketInterval(record.getMarketInterval());
        dto.setOpenTime(record.getOpenTime());
        dto.setCloseTime(record.getCloseTime());
        dto.setOpenPrice(record.getOpenPrice());
        dto.setHighPrice(record.getHighPrice());
        dto.setLowPrice(record.getLowPrice());
        dto.setClosePrice(record.getClosePrice());
        dto.setVolume(record.getVolume());
        dto.setTradeCount(record.getTradeCount());
        return dto;
    }

    private <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data, request.getHeader(TRACE_ID_HEADER));
    }
}
