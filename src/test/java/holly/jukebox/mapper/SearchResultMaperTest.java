package holly.jukebox.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import holly.jukebox.api.dto.SearchResult;
import holly.jukebox.domain.model.Album;
import holly.jukebox.domain.model.ArtistInformation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchResultMaperTest {

  @Mock AlbumDataMapper albumDataMapper;

  @InjectMocks SearchResultMaperImpl searchResultMaper;

  @Test
  void searchResultFromArtistInformation() {
    // Prepare
    Album album = Album.builder().id("album_id").build();
    ArtistInformation artistInformation =
        ArtistInformation.builder()
            .mbid("mbid")
            .description("description")
            .name("Niklas")
            .albums(List.of(album))
            .build();

    // Execute
    SearchResult searchResult =
        searchResultMaper.searchResultFromArtistInformation(artistInformation);

    // Assert
    assertThat(searchResult.mbid()).isEqualTo("mbid");
    assertThat(searchResult.description()).isEqualTo("description");
    verify(albumDataMapper).albumToAlbumData(album);
  }
}
