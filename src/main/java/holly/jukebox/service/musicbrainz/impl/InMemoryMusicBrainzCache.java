package holly.jukebox.service.musicbrainz.impl;

import holly.jukebox.service.musicbrainz.MusicBrainzCache;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Super basic cache to reduce calls towards MusicBrainz.
 *
 * <p>Depending on future requirements this can be refactored to use a real caching framework such
 * as Caffeine or Guava, or a distributed setup with Memcached, Redis, or ValKey.
 */
@Component
class InMemoryMusicBrainzCache implements MusicBrainzCache {
  private final Map<String, String> artistNameToId = new HashMap<>();
  private final Map<String, String> mbidToArtistInformation = new HashMap<>();

  @Override
  public Optional<String> findArtistByName(final String artistName) {
    return Optional.ofNullable(artistNameToId.get(artistName));
  }

  @Override
  public void storeArtistByNameResponse(final String artistName, final String response) {
    artistNameToId.put(artistName, response);
  }

  @Override
  public Optional<String> fetchArtistInformationById(final String mbid) {
    return Optional.ofNullable(mbidToArtistInformation.get(mbid));
  }

  @Override
  public void storeArtistInformationByIdResponse(final String mbid, final String response) {
    mbidToArtistInformation.put(mbid, response);
  }
}
