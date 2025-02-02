package holly.jukebox.service.wiki.impl;

import static org.assertj.core.api.Assertions.assertThat;

import holly.jukebox.service.wiki.WikiCache;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryWikiCacheTest {

  WikiCache wikiCache = new InMemoryWikiCache();

  @Test
  void fetchSitelinksForId() {
    // Prepare
    wikiCache.storeSitelinksForIdResponse("id", "response");

    // Execute
    Optional<String> response = wikiCache.fetchSitelinksForId("id");

    // Assert
    assertThat(response).hasValue("response");
  }

  @Test
  void fetchSitelinksForId_empty() {
    // Execute
    Optional<String> response = wikiCache.fetchSitelinksForId("id");

    // Assert
    assertThat(response).isEmpty();
  }

  @Test
  void fetchDescriptionForTitle() {
    // Prepare
    wikiCache.storeDescriptionForTitleResponse("id", "response");

    // Execute
    Optional<String> response = wikiCache.fetchDescriptionForTitle("id");

    // Assert
    assertThat(response).hasValue("response");
  }

  @Test
  void fetchDescriptionForTitle_empty() {
    // Execute
    Optional<String> response = wikiCache.fetchDescriptionForTitle("id");

    // Assert
    assertThat(response).isEmpty();
  }
}
