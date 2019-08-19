package ee.avalanchelabs.walletserver.service;

import io.grpc.BindableService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;

public interface ServiceGrpc extends BindableService {

    Logger getLogger();

    default void handleException(StreamObserver<?> responseObserver, Runnable runnable) {
        try {
            runnable.run();
        } catch (StatusRuntimeException e) {
            getLogger().debug("handle StatusRuntimeException status = {}", e.getStatus());
            responseObserver.onError(e);
        } catch (Exception e) {
            getLogger().error("handle Exception", e);
            responseObserver.onError(new StatusRuntimeException(Status.UNKNOWN.withDescription(e.getMessage())));
        }
    }
}
