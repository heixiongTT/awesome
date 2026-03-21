package tt.heixiong.awesome.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MarketDataIngestionReq {

    @NotBlank
    private String symbol;

    @NotBlank
    private String interval;

    @Min(1)
    @Max(1000)
    private Integer limit = 200;

    @NotBlank
    private String source = "BINANCE";
}
