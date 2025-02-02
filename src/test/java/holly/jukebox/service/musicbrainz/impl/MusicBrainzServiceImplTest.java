package holly.jukebox.service.musicbrainz.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import holly.jukebox.service.musicbrainz.MusicBrainzRestClient;
import holly.jukebox.service.musicbrainz.model.AlbumResult;
import holly.jukebox.service.musicbrainz.model.ArtistResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MusicBrainzServiceImplTest {

  @Mock MusicBrainzRestClient musicBrainzRestClient;

  @InjectMocks MusicBrainzServiceImpl musicBrainzService;

  @Test
  void findIdByArtistName() throws Exception {
    // Prepare
    String searchResult = readFile("artist_search_result.json");

    // Expect
    when(musicBrainzRestClient.findArtistByName("avicii"))
        .thenReturn(ResponseEntity.ok(searchResult));

    // Execute
    Optional<String> id = musicBrainzService.findIdByArtistName("avicii");

    // Assert
    assertThat(id).hasValue("c85cfd6b-b1e9-4a50-bd55-eb725f04f7d5");
  }

  @Test
  void findIdByArtistNameNoMatch() {
    // Prepare
    String searchResult =
        """
        {
          "created": "2025-01-31T22:22:47.018Z",
          "count": 0,
          "offset": 0,
          "artists": []
        }
        """;

    // Expect
    when(musicBrainzRestClient.findArtistByName("Niklas"))
        .thenReturn(ResponseEntity.ok(searchResult));

    // Execute
    Optional<String> id = musicBrainzService.findIdByArtistName("Niklas");

    // Assert
    assertThat(id).isEmpty();
  }

  static Stream<Arguments> responseEntityErrorResponses() {
    return Stream.of(
        Arguments.of(ResponseEntity.badRequest().build()),
        Arguments.of(ResponseEntity.internalServerError().build()));
  }

  @ParameterizedTest
  @MethodSource("responseEntityErrorResponses")
  void findIdByArtistNameErrors(ResponseEntity<String> response) {
    // Expect
    when(musicBrainzRestClient.findArtistByName("bamse")).thenReturn(response);

    // Execute
    Optional<String> id = musicBrainzService.findIdByArtistName("bamse");

    // Assert
    assertThat(id).isEmpty();
  }

  @Test
  void fetchArtistInformationById() throws Exception {
    // Prepare
    String artistResultResponse = readFile("artist_id_result.json");

    // Expect
    when(musicBrainzRestClient.fetchArtistInformationById("c85cfd6b-b1e9-4a50-bd55-eb725f04f7d5"))
        .thenReturn(ResponseEntity.ok(artistResultResponse));

    // Execute
    Optional<ArtistResult> artistResult =
        musicBrainzService.fetchArtistInformationById("c85cfd6b-b1e9-4a50-bd55-eb725f04f7d5");

    // Assert
    assertThat(artistResult).isPresent();
    assertThat(artistResult.get().name()).isEqualTo("Avicii");
    assertThat(artistResult.get().wikidataId()).hasValue("Q505476");
    assertThat(artistResult.get().wikipediaTitle()).isEmpty();
    assertThat(artistResult.get().albums())
        .containsExactlyInAnyOrder(
            new AlbumResult("61180839-f4a7-407f-b86f-24c48eef4066", "True", "2013-09-13"),
            new AlbumResult("74d25ab8-22c5-4c4d-989d-9b7d37abbe9f", "Stories", "2015-05-13"),
            new AlbumResult("c4a0cc2b-99b6-4299-b35c-92868ba6f570", "TIM", "2019-06-06"),
            new AlbumResult("4877202b-5791-4945-9d5d-adfa7d7da2f2", "The Singles", "2011-05-31"),
            new AlbumResult(
                "119761ec-cdb4-4cd5-be43-d9be2728fc73",
                "Avicii et al: Swedish House Collection – Taken from Superstar",
                "2011-10-28"),
            new AlbumResult(
                "c961be6f-bef2-4861-a9f8-9c872a237256",
                "Avicii Presents Strictly Miami",
                "2011-04-04"),
            new AlbumResult(
                "fd6de1ad-8761-4d92-a45e-ff1a5ff190bc",
                "Onelove Sonic Boom Box 2013",
                "2013-06-24"),
            new AlbumResult("7132049b-ce3b-4627-b6a2-10e169eb0d00", "Live in Osaka", "2016-06-04"),
            new AlbumResult(
                "1538cd31-b8ac-4028-9e17-fec2ccfd2c62", "True (Avicii by Avicii)", "2014-03-24"),
            new AlbumResult(
                "b013dc56-bd74-4815-8e2d-bbd753442057", "Stories (megamix)", "2015-09-26"));
  }

  @Test
  void fetchArtistInformationById_missingWikidata() {
    // Prepare
    String artistResultResponse =
        """
        {
          "name": "Niklas",
          "relations": [],
          "release-groups": []
        }
        """;
    // Expect
    when(musicBrainzRestClient.fetchArtistInformationById("c85cfd6b-b1e9-4a50-bd55-eb725f04f7d5"))
        .thenReturn(ResponseEntity.ok(artistResultResponse));

    // Execute
    Optional<ArtistResult> artistResult =
        musicBrainzService.fetchArtistInformationById("c85cfd6b-b1e9-4a50-bd55-eb725f04f7d5");

    // Assert
    assertThat(artistResult).isPresent();
    assertThat(artistResult.get().name()).isEqualTo("Niklas");
    assertThat(artistResult.get().wikidataId()).isEmpty();
    assertThat(artistResult.get().wikipediaTitle()).isEmpty();
    assertThat(artistResult.get().albums()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("responseEntityErrorResponses")
  void fetchArtistInformationByIdErrors(ResponseEntity<String> response) {
    // Expect
    when(musicBrainzRestClient.fetchArtistInformationById("c85cfd6b-b1e9-4a50-bd55-eb725f04f7d5"))
        .thenReturn(response);

    // Execute
    Optional<ArtistResult> artistResult =
        musicBrainzService.fetchArtistInformationById("c85cfd6b-b1e9-4a50-bd55-eb725f04f7d5");

    // Assert
    assertThat(artistResult).isEmpty();
  }

  static Stream<Arguments> wikidataUrls() {
    return Stream.of(
        Arguments.of("https://www.wikidata.org/wiki/Q15862", "Q15862"),
        Arguments.of("https://www.wikidata.org/wiki/Q15862    ", "Q15862"),
        Arguments.of(null, null),
        Arguments.of("", null),
        Arguments.of(" ", null));
  }

  @ParameterizedTest
  @MethodSource("wikidataUrls")
  void extractWikidataId(String wikidataUrl, String expectedId) {
    // Execute
    String wikidataId = MusicBrainzServiceImpl.extractWikidataId(wikidataUrl);

    // Assert
    assertThat(wikidataId).isEqualTo(expectedId);
  }

  private static String readFile(final String resourceName) throws IOException {
    Path path = Paths.get("src/test/resources/music-brainz/" + resourceName);
    return Files.readString(path);
  }
}
