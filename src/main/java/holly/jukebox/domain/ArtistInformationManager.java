package holly.jukebox.domain;

import holly.jukebox.domain.model.ArtistInformation;

public interface ArtistInformationManager {

  ArtistInformation findArtistInformation(String artistName);
}
