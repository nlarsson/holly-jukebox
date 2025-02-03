package holly.jukebox.service.wiki.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import holly.jukebox.service.wiki.WikipediaRestClient;
import holly.jukebox.service.wiki.config.WikiConfig;
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
@EnableConfigurationProperties(value = WikiConfig.class)
@TestPropertySource(properties = {"wiki.wikipedia.base-url=http://localhost:6060"})
class WikipediaRestClientImplTest {

  @Autowired WikiConfig wikiConfig;

  WikipediaRestClient wikipediaRestClient;

  @BeforeEach
  void setup() {
    wikipediaRestClient =
        new WikipediaRestClientImpl(RestClient.builder(), new NoOpCacheWikiCache(), wikiConfig);
  }

  @Test
  void fetchDescriptionForTitle() {
    // Prepare
    String response =
        """
        {
          "batchcomplete": "",
          "warnings": {
            "extracts": {
              "*": "Warning!"
            }
          },
          "query": {
            "pages": {
              "42010": {
                "pageid": 42010,
                "ns": 0,
                "title": "Queen (band)",
                "extract": "Cool band"
              }
            }
          }
        }
        """;

    // Expect
    WireMock.stubFor(
        get(urlPathEqualTo("/"))
            .withQueryParam("titles", equalTo("Queen (band)"))
            .willReturn(okJson(response)));

    // Execute
    ResponseEntity<String> artistByName =
        wikipediaRestClient.fetchDescriptionForTitle("Queen (band)");

    // Assert
    assertThat(artistByName.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(artistByName.getBody()).isEqualTo(response);
  }
}
