package holly.jukebox.service.musicbrainz.model;

import java.util.List;
import java.util.Optional;

public record ArtistResult(
    String name,
    Optional<String> wikipediaTitle,
    Optional<String> wikidataId,
    List<AlbumResult> albums) {}
