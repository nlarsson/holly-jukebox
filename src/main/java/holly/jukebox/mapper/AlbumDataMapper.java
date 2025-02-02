package holly.jukebox.mapper;

import holly.jukebox.api.dto.AlbumData;
import holly.jukebox.domain.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AlbumDataMapper {
  @Mapping(target = "image", source = "coverArtUrl")
  AlbumData albumToAlbumData(Album album);
}
