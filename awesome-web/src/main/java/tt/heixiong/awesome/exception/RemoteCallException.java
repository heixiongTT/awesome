package tt.heixiong.awesome.exception;

import org.springframework.http.HttpStatus;

public class RemoteCallException extends BusinessException {

    public RemoteCallException(String message) {
        super("REMOTE_CALL_FAILED", message, HttpStatus.BAD_GATEWAY);
    }
}
