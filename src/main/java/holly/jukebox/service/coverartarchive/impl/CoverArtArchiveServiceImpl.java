package holly.jukebox.service.coverartarchive.impl;

import com.jayway.jsonpath.JsonPath;
import holly.jukebox.service.coverartarchive.CoverArtArchiveRestClient;
import holly.jukebox.service.coverartarchive.CoverArtArchiveService;
import holly.jukebox.service.coverartarchive.config.CoverArtArchiveConfig;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoverArtArchiveServiceImpl implements CoverArtArchiveService {
  private static final Logger log = LoggerFactory.getLogger(CoverArtArchiveServiceImpl.class);

  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  private final CoverArtArchiveRestClient client;
  private final CoverArtArchiveConfig config;

  @Override
  public Optional<String> findFrontCoverForId(final String id) {
    log.info("Contacting Cover art archive about front cover for id '{}'", id);
    ResponseEntity<String> response = client.findCoversForId(id);

    if (!response.getStatusCode().is2xxSuccessful()) {
      log.warn(
          "Cover art archive responded with '{}' for id '{}'",
          response.getStatusCode().value(),
          id);
      return Optional.empty();
    }

    final List<String> images =
        JsonPath.read(response.getBody(), "$.images[?(@.front == true)].image");

    return images.stream().findFirst();
  }

  @Override
  public Map<String, Optional<String>> findFrontCoversForIds(Collection<String> ids) {
    final HashMap<String, Future<Optional<String>>> mapping = HashMap.newHashMap(ids.size());

    for (final String id : ids) {
      mapping.put(id, executorService.submit(() -> findFrontCoverForId(id)));
    }

    final HashMap<String, Optional<String>> result = HashMap.newHashMap(ids.size());
    for (final var entry : mapping.entrySet()) {
      final String id = entry.getKey();
      final Optional<String> frontCover = waitForValue(entry);
      result.put(id, frontCover);
    }

    return result;
  }

  private Optional<String> waitForValue(Map.Entry<String, Future<Optional<String>>> entry) {
    try {
      final long timeout = config.requestTimeout().timeout();
      final TimeUnit timeUnit = config.requestTimeout().timeUnit();
      return entry.getValue().get(timeout, timeUnit);
    } catch (InterruptedException e) {
      log.warn("Interrupted", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException | TimeoutException e) {
      log.warn("Issue fetching front cover for id '{}'", entry.getKey(), e);
    }
    return Optional.empty();
  }

  @PreDestroy
  public void destroy() {
    executorService.close();
  }
}
