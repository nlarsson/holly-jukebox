package holly.jukebox.service.coverartarchive.impl;

import holly.jukebox.service.coverartarchive.CoverArtArchiveCache;
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
class InMemoryCoverArtArchiveCache implements CoverArtArchiveCache {
  private final Map<String, String> frontCoverById = new HashMap<>();

  @Override
  public Optional<String> findFrontCoverForId(final String id) {
    return Optional.ofNullable(frontCoverById.get(id));
  }

  @Override
  public void storeFrontCoverForIdResponse(final String id, final String response) {
    frontCoverById.put(id, response);
  }
}
