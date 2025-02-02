package holly.jukebox.service.coverartarchive.impl;

import static org.assertj.core.api.Assertions.assertThat;

import holly.jukebox.service.coverartarchive.CoverArtArchiveCache;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryCoverArtArchiveCacheTest {

  CoverArtArchiveCache coverArtArchiveCache = new InMemoryCoverArtArchiveCache();

  @Test
  void findFrontCoverForId() {
    // Prepare
    coverArtArchiveCache.storeFrontCoverForIdResponse("id", "response");

    // Execute
    Optional<String> response = coverArtArchiveCache.findFrontCoverForId("id");

    // Assert
    assertThat(response).hasValue("response");
  }

  @Test
  void findFrontCoverForId_empty() {
    // Execute
    Optional<String> response = coverArtArchiveCache.findFrontCoverForId("id");

    // Assert
    assertThat(response).isEmpty();
  }
}
