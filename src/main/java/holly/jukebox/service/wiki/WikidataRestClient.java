package holly.jukebox.service.wiki;

import org.springframework.http.ResponseEntity;

/**
 * Thin client to separate actual request from data parsing.
 *
 * <p>Caching should be done here if wanted.
 */
public interface WikidataRestClient {

  ResponseEntity<String> fetchSitelinksForId(String wikidataId);
}
