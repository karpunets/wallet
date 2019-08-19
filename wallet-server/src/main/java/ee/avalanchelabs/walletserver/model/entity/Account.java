package ee.avalanchelabs.walletserver.model.entity;

import ee.avalanchelabs.walletserver.model.AccountCurrency;
import ee.avalanchelabs.walletserver.model.OperationType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet_account", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "currency"}))
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class Account implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;
    @NonNull
    @Column(name = "currency", nullable = false, updatable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private AccountCurrency currency;
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;
    @NonNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    Account(@NonNull User user, @NonNull AccountCurrency currency, @NonNull BigDecimal amount) {
        this(currency, user, amount);
    }

    public void add(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void subtract(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public AccountTransaction newTransaction(@NonNull OperationType type, @NonNull BigDecimal amount) {
        return new AccountTransaction(this, type, currency, amount);
    }
}
