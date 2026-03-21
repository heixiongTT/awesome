package tt.heixiong.awesome.contract;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.dto.MarketDataDto;
import tt.heixiong.awesome.dto.MarketDataIngestionResultDto;
import tt.heixiong.awesome.req.MarketDataIngestionReq;

import javax.validation.Valid;
import java.util.List;

public interface MarketDataContract {

    @PostMapping("/ingestions")
    ApiResponse<MarketDataIngestionResultDto> ingestMarketData(@Valid @RequestBody MarketDataIngestionReq req);

    @GetMapping
    ApiResponse<List<MarketDataDto>> listMarketData(@RequestParam("source") String source,
                                                    @RequestParam("symbol") String symbol,
                                                    @RequestParam("interval") String interval,
                                                    @RequestParam(value = "limit", required = false) Integer limit);
}
