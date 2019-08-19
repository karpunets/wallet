package ee.avalanchelabs.walletclient.round;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RoundManager implements Supplier<Round> {
    private final Random random = new Random();
    private final List<Round> rounds;

    @Override
    public Round get() {
        return rounds.get(random.nextInt(rounds.size()));
    }
}
