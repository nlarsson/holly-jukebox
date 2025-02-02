package holly.jukebox.service.coverartarchive;

import org.springframework.http.ResponseEntity;

/**
 * Thin client to separate actual request from data parsing.
 *
 * <p>Caching should be done here if wanted.
 */
public interface CoverArtArchiveRestClient {

  ResponseEntity<String> findCoversForId(String id);
}
