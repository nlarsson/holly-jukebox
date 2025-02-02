package holly.jukebox.service.wiki.impl;

import holly.jukebox.service.wiki.WikiCache;
import holly.jukebox.service.wiki.WikidataRestClient;
import holly.jukebox.service.wiki.config.WikiConfig;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class WikidataRestClientImpl implements WikidataRestClient {
  private static final Logger log = LoggerFactory.getLogger(WikidataRestClientImpl.class);

  private final RestClient client;
  private final WikiCache wikiCache;

  public WikidataRestClientImpl(
      final RestClient.Builder clientBuilder,
      final WikiCache wikiCache,
      final WikiConfig wikiConfig) {
    String baseUrl = wikiConfig.wikidata().baseUrl();
    client = clientBuilder.baseUrl(baseUrl).build();
    this.wikiCache = wikiCache;
  }

  @Override
  public ResponseEntity<String> fetchSitelinksForId(final String wikidataId) {
    final Optional<String> cachedSitelinksResponse = wikiCache.fetchSitelinksForId(wikidataId);

    if (cachedSitelinksResponse.isPresent()) {
      log.info("Site links for wikidata with id '{}' found in cache", wikidataId);
      return ResponseEntity.ok(cachedSitelinksResponse.get());
    }

    final ResponseEntity<String> response =
        client
            .get()
            .uri(
                "?action=wbgetentities&ids={id}&format=json&props=sitelinks",
                Map.of("id", wikidataId))
            .retrieve()
            .toEntity(String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      wikiCache.storeSitelinksForIdResponse(wikidataId, response.getBody());
    }

    return response;
  }
}
