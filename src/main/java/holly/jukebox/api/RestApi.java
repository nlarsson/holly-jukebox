package holly.jukebox.api;

import holly.jukebox.api.dto.SearchResult;
import holly.jukebox.domain.ArtistInformationManager;
import holly.jukebox.domain.model.ArtistInformation;
import holly.jukebox.mapper.SearchResultMaper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RestApi {
  private static final Logger log = LoggerFactory.getLogger(RestApi.class);

  private final ArtistInformationManager artistInformationManager;
  private final SearchResultMaper searchResultMaper;

  @GetMapping(
      path = "/search/{artistName}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<SearchResult> search(@PathVariable final String artistName) {
    log.info("Searching for artist '{}'", artistName);
    final ArtistInformation artistInformation =
        artistInformationManager.findArtistInformation(artistName);

    final SearchResult searchResult =
        searchResultMaper.searchResultFromArtistInformation(artistInformation);

    return ResponseEntity.ok(searchResult);
  }
}
