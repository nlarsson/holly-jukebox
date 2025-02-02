package holly.jukebox.service.wiki;

import java.util.Map;
import java.util.Optional;

/**
 * This service is responsible for communications with Wiki APIs.
 *
 * <p>More information on the API can be found at:
 * <li>https://www.wikidata.org/w/api.php
 * <li>https://www.mediawiki.org/wiki/API:Main_page
 */
public interface WikiService {

  Optional<Map<String, String>> fetchSitelinksForId(String wikidataId);

  Optional<String> fetchDescriptionForTitle(String wikipediaTitle);
}
