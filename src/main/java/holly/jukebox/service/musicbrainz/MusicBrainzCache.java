package holly.jukebox.service.musicbrainz;

import java.util.Optional;

public interface MusicBrainzCache {
  Optional<String> findArtistByName(String artistName);

  void storeArtistByNameResponse(String artistName, String response);

  Optional<String> fetchArtistInformationById(String mbid);

  void storeArtistInformationByIdResponse(String mbid, String response);
}
