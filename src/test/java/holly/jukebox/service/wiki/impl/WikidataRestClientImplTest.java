package holly.jukebox.service.wiki.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import holly.jukebox.service.wiki.WikidataRestClient;
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
@TestPropertySource(properties = {"wiki.wikidata.base-url=http://localhost:6060"})
class WikidataRestClientImplTest {

  @Autowired WikiConfig wikiConfig;

  WikidataRestClient wikidataRestClient;

  @BeforeEach
  void setup() {
    wikidataRestClient =
        new WikidataRestClientImpl(RestClient.builder(), new NoOpCacheWikiCache(), wikiConfig);
  }

  @Test
  void fetchSitelinksForId() {
    // Prepare
    String response =
        """
        {
          "entities": {
            "Q15862": {
              "type": "item",
              "id": "Q15862",
              "sitelinks": {
                "abwiki": {
                  "site": "abwiki",
                  "title": "Queen",
                  "badges": []
                }
              }
            }
          }
        }
        """;

    // Expect
    WireMock.stubFor(
        get(urlPathEqualTo("/"))
            .withQueryParam("ids", equalTo("Q15862"))
            .willReturn(okJson(response)));

    // Execute
    ResponseEntity<String> artistByName = wikidataRestClient.fetchSitelinksForId("Q15862");

    // Assert
    assertThat(artistByName.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(artistByName.getBody()).isEqualTo(response);
  }
}
