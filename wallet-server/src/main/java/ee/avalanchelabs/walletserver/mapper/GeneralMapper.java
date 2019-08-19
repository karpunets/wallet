package ee.avalanchelabs.walletserver.mapper;

import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public interface GeneralMapper {

    default String uuidToString(UUID uuid) {
        return uuid.toString();
    }

    default UUID stringToUuid(String uuid) {
        return UUID.fromString(uuid);
    }
}
