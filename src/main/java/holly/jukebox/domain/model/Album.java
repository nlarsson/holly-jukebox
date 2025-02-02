package holly.jukebox.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@EqualsAndHashCode
@Getter
@Setter
public class Album {
  private String id;
  private String title;
  private String releaseDate;
  private String coverArtUrl;
}
