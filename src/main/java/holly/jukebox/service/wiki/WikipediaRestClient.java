package holly.jukebox.service.wiki;

import org.springframework.http.ResponseEntity;

/**
 * Thin client to separate actual request from data parsing.
 *
 * <p>Caching should be done here if wanted.
 */
public interface WikipediaRestClient {

  ResponseEntity<String> fetchDescriptionForTitle(String title);
}
