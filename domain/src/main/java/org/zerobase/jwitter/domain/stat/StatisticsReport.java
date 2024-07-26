package org.zerobase.jwitter.domain.stat;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class StatisticsReport {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final Histogram connectionCountHistogram = metricRegistry.
            histogram("connectionCountHistogram");
    private final Timer transactionTimer = metricRegistry.
            timer("transactionTimer");
    private final Slf4jReporter logReporter = Slf4jReporter
            .forRegistry(metricRegistry)
            .outputTo(LOGGER)
            .build();
    public void transactionTime(long nanos) {
        transactionTimer.update(nanos, TimeUnit.NANOSECONDS);
    }
    public void connectionsCount(long count) {
        connectionCountHistogram.update(count);
    }
    public void generate() {
        logReporter.report();
    }
}
