package holly.jukebox.service.wiki.impl;

import holly.jukebox.service.wiki.WikiCache;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Super basic cache to reduce calls towards Wiki sources.
 *
 * <p>Depending on future requirements this can be refactored to use a real caching framework such
 * as Caffeine or Guava, or a distributed setup with Memcached, Redis, or ValKey.
 */
@Component
class InMemoryWikiCache implements WikiCache {
  private final Map<String, String> sitelinksForId = new HashMap<>();
  private final Map<String, String> descrptionForId = new HashMap<>();

  @Override
  public Optional<String> fetchSitelinksForId(final String wikidataId) {
    return Optional.ofNullable(sitelinksForId.get(wikidataId));
  }

  @Override
  public void storeSitelinksForIdResponse(final String wikidataId, final String response) {
    sitelinksForId.put(wikidataId, response);
  }

  @Override
  public Optional<String> fetchDescriptionForTitle(String title) {
    return Optional.ofNullable(descrptionForId.get(title));
  }

  @Override
  public void storeDescriptionForTitleResponse(String title, String response) {
    descrptionForId.put(title, response);
  }
}
