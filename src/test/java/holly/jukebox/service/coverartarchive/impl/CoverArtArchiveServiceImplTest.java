package holly.jukebox.service.coverartarchive.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import holly.jukebox.service.coverartarchive.CoverArtArchiveRestClient;
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
class CoverArtArchiveServiceImplTest {

  @Mock CoverArtArchiveRestClient coverArtArchiveRestClient;

  @InjectMocks CoverArtArchiveServiceImpl coverArtArchiveService;

  @Test
  void findCoversForId() throws Exception {
    // Prepare
    String coversResult = readFile("covers_result.json");

    // Expect
    when(coverArtArchiveRestClient.findCoversForId("id"))
        .thenReturn(ResponseEntity.ok(coversResult));

    // Execute
    Optional<String> frontCover = coverArtArchiveService.findFrontCoverForId("id");

    // Assert
    assertThat(frontCover)
        .hasValue(
            "http://coverartarchive.org/release/81ae60d4-5b75-38df-903a-db2cfa51c2c6/28467016197.jpg");
  }

  static Stream<Arguments> responseEntityErrorResponses() {
    return Stream.of(
        Arguments.of(ResponseEntity.badRequest().build()),
        Arguments.of(ResponseEntity.internalServerError().build()));
  }

  @ParameterizedTest
  @MethodSource("responseEntityErrorResponses")
  void findCoversForIdErrors(ResponseEntity<String> response) {
    // Expect
    when(coverArtArchiveRestClient.findCoversForId("id")).thenReturn(response);

    // Execute
    Optional<String> frontCover = coverArtArchiveService.findFrontCoverForId("id");

    // Assert
    assertThat(frontCover).isEmpty();
  }

  private static String readFile(final String resourceName) throws IOException {
    Path path = Paths.get("src/test/resources/cover-art-archive/" + resourceName);
    return Files.readString(path);
  }
}
