package holly.jukebox.domain.model;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@EqualsAndHashCode
@Getter
@Setter
public class ArtistInformation {
  private String name;
  private String mbid;
  private String description;
  private List<Album> albums;

  public List<Album> getAlbums() {
    if (albums == null) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(albums);
  }
}
