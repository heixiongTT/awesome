CREATE TABLE market_data_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(32) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    market_interval VARCHAR(16) NOT NULL,
    open_time TIMESTAMP NOT NULL,
    close_time TIMESTAMP NOT NULL,
    open_price DECIMAL(20, 8) NOT NULL,
    high_price DECIMAL(20, 8) NOT NULL,
    low_price DECIMAL(20, 8) NOT NULL,
    close_price DECIMAL(20, 8) NOT NULL,
    volume DECIMAL(24, 8) NOT NULL,
    trade_count BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_market_data_unique UNIQUE (source, symbol, market_interval, open_time)
);

CREATE INDEX idx_market_data_lookup ON market_data_records (source, symbol, market_interval, open_time DESC);
