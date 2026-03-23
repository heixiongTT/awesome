package tt.heixiong.awesome.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.dto.ApiErrorResponse;
import tt.heixiong.awesome.dto.MarketDataDto;
import tt.heixiong.awesome.dto.MarketDataIngestionResultDto;
import tt.heixiong.awesome.req.MarketDataIngestionReq;

import java.util.List;

@Api(tags = "Market Data")
@RequestMapping("/market-data")
public interface MarketDataApi {

    @ApiOperation(value = "抓取并存储市场数据", notes = "从配置的数据源抓取 K 线数据，标准化后写入数据库。")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 200, message = "接入成功", response = MarketDataIngestionResultDto.class),
            @io.swagger.annotations.ApiResponse(code = 400, message = "请求参数不合法", response = ApiErrorResponse.class)
    })
    @PostMapping("/ingestions")
    ApiResponse<MarketDataIngestionResultDto> ingestMarketData(
            @ApiParam(value = "市场数据接入请求", required = true,
                    example = "{\"source\":\"BINANCE\",\"symbol\":\"BTCUSDT\",\"interval\":\"1m\",\"limit\":100}")
            @Valid @RequestBody MarketDataIngestionReq req);

    @ApiOperation(value = "查询已存储市场数据", notes = "按数据源、交易对和周期查看已经标准化后的行情记录。")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 200, message = "查询成功", response = MarketDataDto.class, responseContainer = "List")
    })
    @GetMapping
    ApiResponse<List<MarketDataDto>> listMarketData(
            @ApiParam(value = "数据源", example = "BINANCE") @RequestParam("source") String source,
            @ApiParam(value = "交易对", example = "BTCUSDT") @RequestParam("symbol") String symbol,
            @ApiParam(value = "周期", example = "1m") @RequestParam("interval") String interval,
            @ApiParam(value = "返回条数", example = "200") @RequestParam(value = "limit", required = false) Integer limit);
}
