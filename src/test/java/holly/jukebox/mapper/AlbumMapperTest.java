package holly.jukebox.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import holly.jukebox.api.dto.AlbumData;
import holly.jukebox.domain.model.Album;
import org.junit.jupiter.api.Test;

class AlbumMapperTest {

  AlbumDataMapper albumMapper = new AlbumDataMapperImpl();

  @Test
  void albumResultToAlbum() {
    // Prepare
    Album album =
        Album.builder().id("id").title("title").coverArtUrl("http://example.com/image.jpg").build();

    // Execute
    AlbumData albumData = albumMapper.albumToAlbumData(album);

    // Assert
    assertThat(albumData.id()).isEqualTo("id");
    assertThat(albumData.title()).isEqualTo("title");
    assertThat(albumData.image()).isEqualTo("http://example.com/image.jpg");
  }
}
