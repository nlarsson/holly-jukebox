package holly.jukebox.service.musicbrainz.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import holly.jukebox.service.musicbrainz.MusicBrainzCache;
import holly.jukebox.service.musicbrainz.MusicBrainzRestClient;
import holly.jukebox.service.musicbrainz.config.MusicBrainzConfig;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;

@WireMockTest(httpPort = 6060)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = MusicBrainzConfig.class)
@TestPropertySource(
    properties = {
      "music-brainz.base-url=http://localhost:6060",
      "music-brainz.user-agent=test-agent"
    })
class MusicBrainzRestClientImplTest {

  @Autowired MusicBrainzConfig musicBrainzConfig;

  MusicBrainzRestClient musicBrainzRestClient;

  @BeforeEach
  void setup() {
    musicBrainzRestClient =
        new MusicBrainzRestClientImpl(RestClient.builder(), new NoOpCache(), musicBrainzConfig);
  }

  @Test
  void findArtistByName() {
    // Prepare
    String response =
        """
        {
          "artist": "DJ Nicke Lill-Troll",
          "status": "Up-and-coming",
          "id": "abc123"
        }
        """;

    // Expect
    WireMock.stubFor(
        get(urlPathEqualTo("/artist/"))
            .withQueryParam("query", equalTo("artist:DJ Nicke Lill-Troll"))
            .willReturn(okJson(response)));

    // Execute
    ResponseEntity<String> artistByName =
        musicBrainzRestClient.findArtistByName("DJ Nicke Lill-Troll");

    // Assert
    assertThat(artistByName.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(artistByName.getBody()).isEqualTo(response);
  }

  @Test
  void fetchArtistInformationById() {
    // Prepare
    String response =
        """
        {
          "artist": "Dj Nicke Lill-Troll",
          "status": "Up-and-coming",
          "id": "abc123",
        }
        """;

    // Expect
    WireMock.stubFor(
        get(urlPathTemplate("/artist/{mbid}"))
            .withPathParam("mbid", equalTo("abc123"))
            .willReturn(okJson(response)));

    // Execute
    ResponseEntity<String> artistByName =
        musicBrainzRestClient.fetchArtistInformationById("abc123");

    // Assert
    assertThat(artistByName.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(artistByName.getBody()).isEqualTo(response);
  }

  static class NoOpCache implements MusicBrainzCache {
    @Override
    public Optional<String> findArtistByName(String artistName) {
      return Optional.empty();
    }

    @Override
    public void storeArtistByNameResponse(String artistName, String response) {}

    @Override
    public Optional<String> fetchArtistInformationById(String mbid) {
      return Optional.empty();
    }

    @Override
    public void storeArtistInformationByIdResponse(String mbid, String response) {}
  }
}
