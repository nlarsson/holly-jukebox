package holly.jukebox.service.coverartarchive;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * This service is responsible for communications with Cover art archive APIs.
 *
 * <p>More information on the API can be found at https://wiki.musicbrainz.org/Cover_Art_Archive/API
 */
public interface CoverArtArchiveService {

  Optional<String> findFrontCoverForId(String id);

  Map<String, Optional<String>> findFrontCoversForIds(Collection<String> ids);
}
