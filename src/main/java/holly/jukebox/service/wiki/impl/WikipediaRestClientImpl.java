package holly.jukebox.service.wiki.impl;

import holly.jukebox.service.wiki.WikiCache;
import holly.jukebox.service.wiki.WikipediaRestClient;
import holly.jukebox.service.wiki.config.WikiConfig;
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
class WikipediaRestClientImpl implements WikipediaRestClient {
  private static final Logger log = LoggerFactory.getLogger(WikipediaRestClientImpl.class);

  private final RestClient client;
  private final WikiCache wikiCache;

  public WikipediaRestClientImpl(
      final RestClient.Builder clientBuilder,
      final WikiCache wikiCache,
      final WikiConfig wikiConfig) {
    String baseUrl = wikiConfig.wikipedia().baseUrl();
    client = clientBuilder.baseUrl(baseUrl).build();
    this.wikiCache = wikiCache;
  }

  @Override
  public ResponseEntity<String> fetchDescriptionForTitle(final String title) {
    final Optional<String> cachedSitelinksResponse = wikiCache.fetchDescriptionForTitle(title);

    if (cachedSitelinksResponse.isPresent()) {
      log.info("Description for wikipedia title '{}' found in cache", title);
      return ResponseEntity.ok(cachedSitelinksResponse.get());
    }

    final ResponseEntity<String> response =
        client
            .get()
            .uri(
                "?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles={title}",
                Map.of("title", title))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::onlyLogResponseError)
            .onStatus(HttpStatusCode::is5xxServerError, this::onlyLogResponseError)
            .toEntity(String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      wikiCache.storeDescriptionForTitleResponse(title, response.getBody());
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
