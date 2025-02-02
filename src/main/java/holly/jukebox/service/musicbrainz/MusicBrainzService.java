package holly.jukebox.service.musicbrainz;

import holly.jukebox.service.musicbrainz.model.ArtistResult;
import java.util.Optional;

/**
 * This service is responsible for communications with MusicBrainz API.
 *
 * <p>More information on the API can be found at https://musicbrainz.org/doc/MusicBrainz_API
 */
public interface MusicBrainzService {

  Optional<String> findIdByArtistName(String artistName);

  Optional<ArtistResult> fetchArtistInformationById(String mbid);
}
