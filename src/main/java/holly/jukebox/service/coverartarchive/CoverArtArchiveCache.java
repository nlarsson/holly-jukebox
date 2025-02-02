package holly.jukebox.service.coverartarchive;

import java.util.Optional;

public interface CoverArtArchiveCache {
  Optional<String> findFrontCoverForId(String id);

  void storeFrontCoverForIdResponse(String id, String response);
}
