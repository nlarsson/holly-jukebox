package holly.jukebox.service.musicbrainz;

import org.springframework.http.ResponseEntity;

/**
 * Thin client to separate actual request from data parsing.
 *
 * <p>Caching should be done here if wanted.
 */
public interface MusicBrainzRestClient {

  ResponseEntity<String> findArtistByName(String name);

  ResponseEntity<String> fetchArtistInformationById(String mbid);
}
