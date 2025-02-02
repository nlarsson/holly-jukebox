package holly.jukebox.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles all exceptions that arise within {@link holly.jukebox.api.RestApi} (because that's the
 * only component with @RestController).
 *
 * <p>This can be elaborated on to handle cases where a more specific message/action is more
 * appropriate.
 */
@RestControllerAdvice(annotations = RestController.class)
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(RestApiExceptionHandler.class);

  @ExceptionHandler
  protected ResponseEntity<Object> handle(final Exception exception, final WebRequest request) {
    log.warn("Exception occurred", exception);
    return handleExceptionInternal(
        exception,
        "Internal server error",
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }
}
