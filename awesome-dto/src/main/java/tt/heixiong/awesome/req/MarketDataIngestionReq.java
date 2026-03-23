package tt.heixiong.awesome.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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
