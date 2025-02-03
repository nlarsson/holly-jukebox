package holly.jukebox.service.coverartarchive.impl;

import holly.jukebox.service.coverartarchive.CoverArtArchiveCache;
import holly.jukebox.service.coverartarchive.CoverArtArchiveRestClient;
import holly.jukebox.service.coverartarchive.config.CoverArtArchiveConfig;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class CoverArtArchiveRestClientImpl implements CoverArtArchiveRestClient {
  private static final Logger log = LoggerFactory.getLogger(CoverArtArchiveRestClientImpl.class);

  private final RestClient client;
  private final CoverArtArchiveCache coverArtArchiveCache;

  public CoverArtArchiveRestClientImpl(
      final RestClient.Builder clientBuilder,
      final CoverArtArchiveCache coverArtArchiveCache,
      final CoverArtArchiveConfig coverArtArchiveConfig) {
    this.client = clientBuilder.baseUrl(coverArtArchiveConfig.baseUrl()).build();
    this.coverArtArchiveCache = coverArtArchiveCache;
  }

  @Override
  public ResponseEntity<String> findCoversForId(final String id) {
    final Optional<String> cachedFrontCoverResponse = coverArtArchiveCache.findFrontCoverForId(id);
    if (cachedFrontCoverResponse.isPresent()) {
      log.info("Front cover for id '{}' found in cache", id);
      return ResponseEntity.ok(cachedFrontCoverResponse.get());
    }

    log.info("Outgoing call to Cover art archive for front cover with id '{}'", id);
    final ResponseEntity<String> response =
        client
            .get()
            .uri("/release-group/{id}", Map.of("id", id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::onlyLogResponseError)
            .onStatus(HttpStatusCode::is5xxServerError, this::onlyLogResponseError)
            .toEntity(String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      coverArtArchiveCache.storeFrontCoverForIdResponse(id, response.getBody());
    }

    return response;
  }

  private void onlyLogResponseError(HttpRequest request, ClientHttpResponse response)
      throws IOException {
    log.warn(
        "MusicBrainz responded with '{}': {}",
        response.getStatusCode().value(),
        new String(response.getBody().readAllBytes()));
  }
}
