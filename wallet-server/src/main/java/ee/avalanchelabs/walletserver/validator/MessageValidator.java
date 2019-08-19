package ee.avalanchelabs.walletserver.validator;

import com.google.protobuf.Message;

public interface MessageValidator<T extends Message> {

    Class<T> getSupportedClass();

    void validate(T message);
}
