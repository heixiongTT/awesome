package tt.heixiong.awesome.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "market_data_records")
public class MarketDataRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String source;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(name = "market_interval", nullable = false, length = 16)
    private String marketInterval;

    @Column(nullable = false)
    private Instant openTime;

    @Column(nullable = false)
    private Instant closeTime;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal lowPrice;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal closePrice;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal volume;

    @Column(nullable = false)
    private Long tradeCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
