package ee.avalanchelabs.walletserver.mapper;

import ee.avalanchelabs.wallet.proto.FundsRequest;
import ee.avalanchelabs.wallet.proto.TransactionDto;
import ee.avalanchelabs.walletserver.model.entity.AccountTransaction;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(uses = GeneralMapper.class)
public interface WalletMapper {

    TransactionDto toDto(AccountTransaction transaction);

    default BigDecimal amountToBigDecimal(FundsRequest request) {
        return new BigDecimal(request.getAmount());
    }
}
