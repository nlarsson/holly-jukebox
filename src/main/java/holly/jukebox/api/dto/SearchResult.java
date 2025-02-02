package holly.jukebox.api.dto;

import java.util.List;

public record SearchResult(String mbid, String name, String description, List<AlbumData> albums) {}
