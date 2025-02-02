package holly.jukebox.service.musicbrainz.impl;

import static org.assertj.core.api.Assertions.assertThat;

import holly.jukebox.service.musicbrainz.MusicBrainzCache;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryMusicBrainzCacheTest {

  MusicBrainzCache musicBrainzCache = new InMemoryMusicBrainzCache();

  @Test
  void fetchArtistInformationById() {
    // Prepare
    musicBrainzCache.storeArtistInformationByIdResponse("id", "response");

    // Execute
    Optional<String> response = musicBrainzCache.fetchArtistInformationById("id");

    // Assert
    assertThat(response).hasValue("response");
  }

  @Test
  void fetchArtistInformationById_empty() {
    // Execute
    Optional<String> response = musicBrainzCache.fetchArtistInformationById("id");

    // Assert
    assertThat(response).isEmpty();
  }

  @Test
  void findArtistByName() {
    // Prepare
    musicBrainzCache.storeArtistByNameResponse("name", "response");

    // Execute
    Optional<String> response = musicBrainzCache.findArtistByName("name");

    // Assert
    assertThat(response).hasValue("response");
  }

  @Test
  void findArtistByName_empty() {
    // Execute
    Optional<String> response = musicBrainzCache.findArtistByName("name");

    // Assert
    assertThat(response).isEmpty();
  }
}
