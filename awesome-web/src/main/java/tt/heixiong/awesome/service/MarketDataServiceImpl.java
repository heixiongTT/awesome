package tt.heixiong.awesome.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tt.heixiong.awesome.domain.MarketDataRecord;
import tt.heixiong.awesome.dto.MarketDataIngestionResultDto;
import tt.heixiong.awesome.exception.BusinessException;
import tt.heixiong.awesome.market.MarketDataCandle;
import tt.heixiong.awesome.market.MarketDataSourceClient;
import tt.heixiong.awesome.repository.MarketDataRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketDataServiceImpl implements MarketDataService {

    private final List<MarketDataSourceClient> marketDataSourceClients;
    private final MarketDataRepository marketDataRepository;

    public MarketDataServiceImpl(List<MarketDataSourceClient> marketDataSourceClients,
                                 MarketDataRepository marketDataRepository) {
        this.marketDataSourceClients = marketDataSourceClients;
        this.marketDataRepository = marketDataRepository;
    }

    @Override
    public MarketDataIngestionResultDto ingest(String source, String symbol, String interval, int limit) {
        String normalizedSource = normalizeRequired(source, "source").toUpperCase();
        String normalizedSymbol = normalizeRequired(symbol, "symbol").toUpperCase();
        String normalizedInterval = normalizeRequired(interval, "interval").toLowerCase();
        if (limit < 1 || limit > 1000) {
            throw new BusinessException("INVALID_MARKET_DATA_LIMIT", "limit must be between 1 and 1000", HttpStatus.BAD_REQUEST);
        }

        MarketDataSourceClient client = marketDataSourceClients.stream()
                .filter(candidate -> candidate.supports(normalizedSource))
                .findFirst()
                .orElseThrow(() -> new BusinessException("UNSUPPORTED_MARKET_DATA_SOURCE",
                        "Unsupported market data source: " + normalizedSource,
                        HttpStatus.BAD_REQUEST));

        List<MarketDataCandle> fetchedCandles = client.fetchCandles(normalizedSymbol, normalizedInterval, limit);
        int inserted = 0;
        int duplicates = 0;
        List<Instant> openTimes = new ArrayList<Instant>();
        for (MarketDataCandle candle : fetchedCandles) {
            openTimes.add(candle.getOpenTime());
            if (marketDataRepository.existsBySourceAndSymbolAndMarketIntervalAndOpenTime(
                    candle.getSource(), candle.getSymbol(), candle.getMarketInterval(), candle.getOpenTime())) {
                duplicates++;
                continue;
            }
            marketDataRepository.save(toRecord(candle));
            inserted++;
        }

        MarketDataIngestionResultDto resultDto = new MarketDataIngestionResultDto();
        resultDto.setSource(normalizedSource);
        resultDto.setSymbol(normalizedSymbol);
        resultDto.setMarketInterval(normalizedInterval);
        resultDto.setRequested(limit);
        resultDto.setFetched(fetchedCandles.size());
        resultDto.setInserted(inserted);
        resultDto.setDuplicates(duplicates);
        if (!openTimes.isEmpty()) {
            resultDto.setEarliestOpenTime(openTimes.stream().min(Comparator.naturalOrder()).get());
            resultDto.setLatestOpenTime(openTimes.stream().max(Comparator.naturalOrder()).get());
        }
        return resultDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketDataRecord> list(String source, String symbol, String interval, Integer limit) {
        String normalizedSource = normalizeRequired(source, "source").toUpperCase();
        String normalizedSymbol = normalizeRequired(symbol, "symbol").toUpperCase();
        String normalizedInterval = normalizeRequired(interval, "interval").toLowerCase();
        MarketDataAggregationSupport.resolveInterval(normalizedInterval);
        validateListLimit(limit);

        List<MarketDataRecord> records = marketDataRepository.findTop200BySourceAndSymbolAndMarketIntervalOrderByOpenTimeDesc(
                normalizedSource,
                normalizedSymbol,
                normalizedInterval);
        if (!records.isEmpty()) {
            return applyLimit(records, limit);
        }

        List<String> lowerIntervalCandidates = MarketDataAggregationSupport.findLowerIntervalCandidates(normalizedInterval);
        if (lowerIntervalCandidates.isEmpty()) {
            return Collections.emptyList();
        }

        List<MarketDataRecord> candidateRecords = marketDataRepository.findTop1000BySourceAndSymbolAndMarketIntervalInOrderByOpenTimeDesc(
                normalizedSource,
                normalizedSymbol,
                lowerIntervalCandidates);
        Optional<String> sourceInterval = MarketDataAggregationSupport.pickBestSourceInterval(candidateRecords, normalizedInterval);
        if (!sourceInterval.isPresent()) {
            return Collections.emptyList();
        }

        List<MarketDataRecord> recordsToAggregate = candidateRecords.stream()
                .filter(record -> sourceInterval.get().equals(record.getMarketInterval()))
                .collect(Collectors.toList());
        return MarketDataAggregationSupport.aggregate(recordsToAggregate, normalizedInterval, limit);
    }

    private void validateListLimit(Integer limit) {
        if (limit != null && limit < 1) {
            throw new BusinessException("INVALID_MARKET_DATA_LIMIT", "limit must be greater than 0", HttpStatus.BAD_REQUEST);
        }
    }

    private List<MarketDataRecord> applyLimit(List<MarketDataRecord> records, Integer limit) {
        if (limit == null || limit >= records.size()) {
            return records;
        }
        return records.subList(0, limit);
    }

    private MarketDataRecord toRecord(MarketDataCandle candle) {
        MarketDataRecord record = new MarketDataRecord();
        record.setSource(candle.getSource());
        record.setSymbol(candle.getSymbol());
        record.setMarketInterval(candle.getMarketInterval());
        record.setOpenTime(candle.getOpenTime());
        record.setCloseTime(candle.getCloseTime());
        record.setOpenPrice(candle.getOpenPrice());
        record.setHighPrice(candle.getHighPrice());
        record.setLowPrice(candle.getLowPrice());
        record.setClosePrice(candle.getClosePrice());
        record.setVolume(candle.getVolume());
        record.setTradeCount(candle.getTradeCount());
        return record;
    }

    private String normalizeRequired(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("INVALID_MARKET_DATA_REQUEST", fieldName + " must not be blank", HttpStatus.BAD_REQUEST);
        }
        return value.trim();
    }
}
