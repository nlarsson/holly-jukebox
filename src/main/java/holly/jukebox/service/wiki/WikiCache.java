package holly.jukebox.service.wiki;

import java.util.Optional;

public interface WikiCache {

  Optional<String> fetchSitelinksForId(String wikidataId);

  void storeSitelinksForIdResponse(String wikidataId, String response);

  Optional<String> fetchDescriptionForTitle(String title);

  void storeDescriptionForTitleResponse(String title, String response);
}
