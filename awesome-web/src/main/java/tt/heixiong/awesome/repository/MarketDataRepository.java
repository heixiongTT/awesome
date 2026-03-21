package tt.heixiong.awesome.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tt.heixiong.awesome.domain.MarketDataRecord;

import java.time.Instant;
import java.util.List;

public interface MarketDataRepository extends JpaRepository<MarketDataRecord, Long> {

    boolean existsBySourceAndSymbolAndMarketIntervalAndOpenTime(String source,
                                                                String symbol,
                                                                String marketInterval,
                                                                Instant openTime);

    List<MarketDataRecord> findTop200BySourceAndSymbolAndMarketIntervalOrderByOpenTimeDesc(String source,
                                                                                            String symbol,
                                                                                            String marketInterval);
}
