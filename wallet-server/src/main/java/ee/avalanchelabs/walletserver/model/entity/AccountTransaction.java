package ee.avalanchelabs.walletserver.model.entity;

import ee.avalanchelabs.walletserver.model.AccountCurrency;
import ee.avalanchelabs.walletserver.model.OperationType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_transaction")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "account")
public class AccountTransaction implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;
    @NonNull
    @Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private OperationType type;
    @NonNull
    @Column(name = "currency", nullable = false, updatable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private AccountCurrency currency;
    @NonNull
    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;
    @Column(name = "created", nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();
}
