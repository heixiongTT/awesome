package tt.heixiong.awesome.service;

import tt.heixiong.awesome.domain.MarketDataRecord;
import tt.heixiong.awesome.dto.MarketDataIngestionResultDto;

import java.util.List;

public interface MarketDataService {

    MarketDataIngestionResultDto ingest(String source, String symbol, String interval, int limit);

    List<MarketDataRecord> list(String source, String symbol, String interval, Integer limit);
}
