package holly.jukebox.mapper;

import holly.jukebox.api.dto.SearchResult;
import holly.jukebox.domain.model.ArtistInformation;
import org.mapstruct.Mapper;

@Mapper(uses = AlbumDataMapper.class)
public interface SearchResultMaper {

  SearchResult searchResultFromArtistInformation(ArtistInformation artistInformation);
}
