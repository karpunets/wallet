package ee.avalanchelabs.walletserver.service;

import com.google.common.collect.ImmutableMap;
import ee.avalanchelabs.wallet.proto.*;
import ee.avalanchelabs.walletserver.service.impl.UserServiceGrpc;
import ee.avalanchelabs.walletserver.service.impl.WalletServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static ee.avalanchelabs.wallet.proto.Currency.EUR;
import static ee.avalanchelabs.wallet.proto.Currency.USD;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(properties = "wallet.server.active=false")
public class WalletServiceGrpcTest {
    private static final Empty EMPTY = Empty.newBuilder().build();
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    @Autowired
    private UserServiceGrpc userServiceGrpc;
    @Autowired
    private WalletServiceGrpc walletServiceGrpc;
    private UserGrpc.UserBlockingStub userBlockingStub;
    private WalletGrpc.WalletBlockingStub walletBlockingStub;

    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        Server server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(userServiceGrpc)
                .addService(walletServiceGrpc)
                .build();
        grpcCleanup.register(server.start());
        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        ManagedChannel registeredChannel = grpcCleanup.register(channel);
        userBlockingStub = UserGrpc.newBlockingStub(registeredChannel);
        walletBlockingStub = WalletGrpc.newBlockingStub(registeredChannel);
    }

    @Test
    public void integration() {
        String userId = userBlockingStub.create(EMPTY).getId();
        assertFalse(accounts(userId).hasNext());

        insufficientFunds(() -> walletBlockingStub.withdraw(funds(userId, 200, USD)));
        walletBlockingStub.deposit(funds(userId, 100, USD));
        assertAccounts(userId, ImmutableMap.of(USD, 100d));

        insufficientFunds(() -> walletBlockingStub.withdraw(funds(userId, 200, USD)));
        walletBlockingStub.deposit(funds(userId, 100, EUR));
        assertAccounts(userId, ImmutableMap.of(USD, 100d, EUR, 100d));

        insufficientFunds(() -> walletBlockingStub.withdraw(funds(userId, 200, USD)));
        walletBlockingStub.deposit(funds(userId, 100, USD));
        assertAccounts(userId, ImmutableMap.of(USD, 200d, EUR, 100d));

        walletBlockingStub.withdraw(funds(userId, 200, USD));
        assertAccounts(userId, ImmutableMap.of(USD, 0d, EUR, 100d));

        insufficientFunds(() -> walletBlockingStub.withdraw(funds(userId, 200, USD)));
    }

    @Test(expected = StatusRuntimeException.class)
    public void whenGetBalanceForNonexistentUser() {
        accounts(UUID.randomUUID().toString()).next();
    }

    private void assertAccounts(String userId, Map<Currency, Double> expectedAccount) {
        Iterator<AccountDto> iterator = accounts(userId);
        if (!expectedAccount.isEmpty()) {
            assertTrue(iterator.hasNext());
        }
        iterator.forEachRemaining(a -> {
            BigDecimal amount = new BigDecimal(expectedAccount.get(a.getCurrency()));
            BigDecimal expected = new BigDecimal(a.getAmount());
            assertEquals(expected + " != " + amount, 0, amount.compareTo(expected));
        });
    }

    private Iterator<AccountDto> accounts(String userId) {
        return walletBlockingStub.balance(UserDto.newBuilder().setId(userId).build());
    }

    private FundsRequest funds(String userId, double v, Currency currency) {
        return FundsRequest.newBuilder()
                .setUserId(userId)
                .setAmount(new BigDecimal(v).toString())
                .setCurrency(currency)
                .build();
    }

    private void insufficientFunds(Runnable runnable) {
        try {
            runnable.run();
            fail("not throw exception");
        } catch (StatusRuntimeException e) {
            assertEquals(Status.FAILED_PRECONDITION, e.getStatus());
        }
    }
}
