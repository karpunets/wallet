package ee.avalanchelabs.walletclient.metric;

import ee.avalanchelabs.walletclient.properties.WalletCliProperties;
import ee.avalanchelabs.walletclient.round.Round;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
public class RoundMetric implements ApplicationRunner {
    private final WalletCliProperties properties;
    private final Map<Class<? extends Round>, AtomicLong> roundCounted = new HashMap<>();
    private final AtomicInteger errors = new AtomicInteger();
    private LocalTime startTime;

    @Override
    public void run(ApplicationArguments args) {
        startTime = LocalTime.now();
    }

    public void request(Class<? extends Round> roundClass) {
        roundCounted.computeIfAbsent(roundClass, c -> new AtomicLong()).incrementAndGet();
    }

    public void error() {
        errors.incrementAndGet();
    }

    @PreDestroy
    public void printMetrics() {
        LocalTime endTime = LocalTime.now();
        log.warn("--------- RoundMetric ---------");
        log.warn("Properties: Users = {}; Thread = {}; Round = {}",
                properties.getUsers(), properties.getConcurrentThreadsPerUser(), properties.getRoundsPerThread());
        log.warn("Start = {}; End = {}", startTime, endTime);
        long sum = roundCounted.values().stream().mapToLong(AtomicLong::get).sum();
        long duration = ChronoUnit.SECONDS.between(startTime, endTime);
        log.warn("Total {} requests in {} seconds ({} per second)", sum, duration, duration == 0 ? "-" : (sum / duration));
        roundCounted.forEach((roundClass, counted) ->
                log.warn("{} makes {} requests", roundClass.getSimpleName(), counted.get()));
        log.warn("Error = {}", errors);

    }
}
