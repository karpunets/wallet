package ee.avalanchelabs.walletserver.model.entity;

import ee.avalanchelabs.walletserver.model.AccountCurrency;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet_user")
@Data
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    public Account newAccount(@NonNull AccountCurrency currency, @NonNull BigDecimal amount) {
        return new Account(this, currency, amount);
    }
}
