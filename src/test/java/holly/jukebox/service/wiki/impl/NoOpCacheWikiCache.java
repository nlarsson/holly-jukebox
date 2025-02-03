package holly.jukebox.service.wiki.impl;

import holly.jukebox.service.wiki.WikiCache;
import java.util.Optional;

class NoOpCacheWikiCache implements WikiCache {
  @Override
  public Optional<String> fetchSitelinksForId(String wikidataId) {
    return Optional.empty();
  }

  @Override
  public void storeSitelinksForIdResponse(String wikidataId, String response) {}

  @Override
  public Optional<String> fetchDescriptionForTitle(String title) {
    return Optional.empty();
  }

  @Override
  public void storeDescriptionForTitleResponse(String title, String response) {}
}
