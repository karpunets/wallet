package ee.avalanchelabs.walletserver.validator;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.grpc.*;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class GrpcValidationInterceptor implements ServerInterceptor {
    private final Collection<MessageValidator<?>> validators;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            @Override
            public void onMessage(ReqT message) {
                GeneratedMessageV3 messageV3 = (GeneratedMessageV3) message;
                try {
                    validators.forEach(validator -> validate(validator, messageV3));
                    super.onMessage(message);
                } catch (StatusRuntimeException e) {
                    call.close(e.getStatus(), headers);
                    throw e;
                } catch (Exception e) {
                    Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                    call.close(status, headers);
                    throw new StatusRuntimeException(status);
                }
            }
        };
    }

    private <T extends Message> void validate(MessageValidator<T> validator, Message message) {
        Class<T> supportedClass = validator.getSupportedClass();
        if (message.getClass().isAssignableFrom(supportedClass)) {
            T supportedMessage = supportedClass.cast(message);
            validator.validate(supportedMessage);
        }
    }
}
