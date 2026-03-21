package tt.heixiong.awesome.service;

import org.springframework.http.HttpStatus;
import tt.heixiong.awesome.domain.MarketDataRecord;
import tt.heixiong.awesome.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

final class MarketDataAggregationSupport {

    private static final List<IntervalDefinition> SUPPORTED_INTERVALS = Arrays.asList(
            new IntervalDefinition("1m", 60),
            new IntervalDefinition("3m", 180),
            new IntervalDefinition("5m", 300),
            new IntervalDefinition("15m", 900),
            new IntervalDefinition("30m", 1800),
            new IntervalDefinition("1h", 3600),
            new IntervalDefinition("4h", 14400),
            new IntervalDefinition("1d", 86400));

    private MarketDataAggregationSupport() {
    }

    static IntervalDefinition resolveInterval(String interval) {
        return SUPPORTED_INTERVALS.stream()
                .filter(candidate -> candidate.getCode().equals(interval))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "UNSUPPORTED_MARKET_DATA_INTERVAL",
                        "Unsupported market data interval: " + interval,
                        HttpStatus.BAD_REQUEST));
    }

    static List<String> findLowerIntervalCandidates(String targetInterval) {
        IntervalDefinition target = resolveInterval(targetInterval);
        List<String> candidates = SUPPORTED_INTERVALS.stream()
                .filter(candidate -> target.getSeconds() % candidate.getSeconds() == 0)
                .filter(candidate -> candidate.getSeconds() < target.getSeconds())
                .sorted(Comparator.comparingLong(IntervalDefinition::getSeconds).reversed())
                .map(IntervalDefinition::getCode)
                .collect(Collectors.toList());
        return candidates;
    }

    static List<MarketDataRecord> aggregate(List<MarketDataRecord> sourceRecords, String targetInterval, Integer limit) {
        if (sourceRecords.isEmpty()) {
            return Collections.emptyList();
        }
        IntervalDefinition target = resolveInterval(targetInterval);
        Map<Instant, List<MarketDataRecord>> grouped = new LinkedHashMap<Instant, List<MarketDataRecord>>();
        List<MarketDataRecord> sortedRecords = new ArrayList<MarketDataRecord>(sourceRecords);
        sortedRecords.sort(Comparator.comparing(MarketDataRecord::getOpenTime));
        for (MarketDataRecord record : sortedRecords) {
            Instant bucketOpenTime = truncate(record.getOpenTime(), target.getSeconds());
            grouped.computeIfAbsent(bucketOpenTime, key -> new ArrayList<MarketDataRecord>()).add(record);
        }

        List<MarketDataRecord> aggregated = grouped.entrySet().stream()
                .map(entry -> aggregateBucket(entry.getValue(), entry.getKey(), target))
                .sorted(Comparator.comparing(MarketDataRecord::getOpenTime).reversed())
                .collect(Collectors.toList());
        if (limit == null || limit >= aggregated.size()) {
            return aggregated;
        }
        return aggregated.subList(0, limit);
    }

    static Optional<String> pickBestSourceInterval(Collection<MarketDataRecord> records, String targetInterval) {
        IntervalDefinition target = resolveInterval(targetInterval);
        return records.stream()
                .map(MarketDataRecord::getMarketInterval)
                .distinct()
                .map(MarketDataAggregationSupport::resolveInterval)
                .filter(candidate -> target.getSeconds() % candidate.getSeconds() == 0)
                .filter(candidate -> candidate.getSeconds() < target.getSeconds())
                .max(Comparator.comparingLong(IntervalDefinition::getSeconds))
                .map(IntervalDefinition::getCode);
    }

    private static MarketDataRecord aggregateBucket(List<MarketDataRecord> bucket,
                                                    Instant bucketOpenTime,
                                                    IntervalDefinition target) {
        List<MarketDataRecord> sortedBucket = new ArrayList<MarketDataRecord>(bucket);
        sortedBucket.sort(Comparator.comparing(MarketDataRecord::getOpenTime));
        MarketDataRecord first = sortedBucket.get(0);
        MarketDataRecord last = sortedBucket.get(sortedBucket.size() - 1);

        MarketDataRecord aggregated = new MarketDataRecord();
        aggregated.setSource(first.getSource());
        aggregated.setSymbol(first.getSymbol());
        aggregated.setMarketInterval(target.getCode());
        aggregated.setOpenTime(bucketOpenTime);
        aggregated.setCloseTime(bucketOpenTime.plusSeconds(target.getSeconds() - 1));
        aggregated.setOpenPrice(first.getOpenPrice());
        aggregated.setClosePrice(last.getClosePrice());
        aggregated.setHighPrice(sortedBucket.stream()
                .map(MarketDataRecord::getHighPrice)
                .max(BigDecimal::compareTo)
                .get());
        aggregated.setLowPrice(sortedBucket.stream()
                .map(MarketDataRecord::getLowPrice)
                .min(BigDecimal::compareTo)
                .get());
        aggregated.setVolume(sortedBucket.stream()
                .map(MarketDataRecord::getVolume)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        aggregated.setTradeCount(sortedBucket.stream()
                .map(MarketDataRecord::getTradeCount)
                .reduce(0L, Long::sum));
        return aggregated;
    }

    private static Instant truncate(Instant instant, long intervalSeconds) {
        long epochSecond = instant.getEpochSecond();
        return Instant.ofEpochSecond(epochSecond - epochSecond % intervalSeconds);
    }

    static final class IntervalDefinition {
        private final String code;
        private final long seconds;

        IntervalDefinition(String code, long seconds) {
            this.code = code;
            this.seconds = seconds;
        }

        String getCode() {
            return code;
        }

        long getSeconds() {
            return seconds;
        }
    }
}
