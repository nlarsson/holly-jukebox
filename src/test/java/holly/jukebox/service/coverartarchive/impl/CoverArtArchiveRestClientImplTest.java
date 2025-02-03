package holly.jukebox.service.coverartarchive.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import holly.jukebox.service.coverartarchive.CoverArtArchiveCache;
import holly.jukebox.service.coverartarchive.CoverArtArchiveRestClient;
import holly.jukebox.service.coverartarchive.config.CoverArtArchiveConfig;
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
@EnableConfigurationProperties(value = CoverArtArchiveConfig.class)
@TestPropertySource(properties = {"cover-art-archive.base-url=http://localhost:6060"})
class CoverArtArchiveRestClientImplTest {

  @Autowired CoverArtArchiveConfig coverArtArchiveConfig;

  CoverArtArchiveRestClient coverArtArchiveRestClient;

  @BeforeEach
  void setup() {
    coverArtArchiveRestClient =
        new CoverArtArchiveRestClientImpl(
            RestClient.builder(), new NoOpCache(), coverArtArchiveConfig);
  }

  @Test
  void findCoversForId() {
    // Prepare
    String response =
        """
        {
          "images": [
            {
              "approved": true,
              "back": false,
              "comment": "",
              "edit": 76926598,
              "front": true,
              "id": 28467016197,
              "image": "http://coverartarchive.org/release/81ae60d4-5b75-38df-903a-db2cfa51c2c6/28467016197.jpg",
              "thumbnails": {
                "1200": "http://coverartarchive.org/release/81ae60d4-5b75-38df-903a-db2cfa51c2c6/28467016197-1200.jpg",
                "250": "http://coverartarchive.org/release/81ae60d4-5b75-38df-903a-db2cfa51c2c6/28467016197-250.jpg",
                "500": "http://coverartarchive.org/release/81ae60d4-5b75-38df-903a-db2cfa51c2c6/28467016197-500.jpg",
                "large": "http://coverartarchive.org/release/81ae60d4-5b75-38df-903a-db2cfa51c2c6/28467016197-500.jpg",
                "small": "http://coverartarchive.org/release/81ae60d4-5b75-38df-903a-db2cfa51c2c6/28467016197-250.jpg"
              },
              "types": [
                "Front"
              ]
            },
          ],
        }
        """;

    // Expect
    WireMock.stubFor(
        get(urlPathTemplate("/release-group/{id}"))
            .withPathParam("id", equalTo("album-id"))
            .willReturn(okJson(response)));

    // Execute
    ResponseEntity<String> artistByName = coverArtArchiveRestClient.findCoversForId("album-id");

    // Assert
    assertThat(artistByName.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(artistByName.getBody()).isEqualTo(response);
  }

  static class NoOpCache implements CoverArtArchiveCache {
    @Override
    public Optional<String> findFrontCoverForId(String id) {
      return Optional.empty();
    }

    @Override
    public void storeFrontCoverForIdResponse(String id, String response) {}
  }
}
