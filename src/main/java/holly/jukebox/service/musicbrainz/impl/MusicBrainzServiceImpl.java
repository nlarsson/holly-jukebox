package holly.jukebox.service.musicbrainz.impl;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import holly.jukebox.service.musicbrainz.MusicBrainzRestClient;
import holly.jukebox.service.musicbrainz.MusicBrainzService;
import holly.jukebox.service.musicbrainz.model.AlbumResult;
import holly.jukebox.service.musicbrainz.model.ArtistResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicBrainzServiceImpl implements MusicBrainzService {
  private static final Logger log = LoggerFactory.getLogger(MusicBrainzServiceImpl.class);

  private final MusicBrainzRestClient client;

  @Override
  public Optional<String> findIdByArtistName(final String artistName) {
    log.info("Contacting MusicBrainz about artist '{}", artistName);
    final ResponseEntity<String> response = client.findArtistByName(artistName);

    if (!response.getStatusCode().is2xxSuccessful()) {
      log.warn(
          "MusicBraniz responded with '{}' for artist '{}'",
          response.getStatusCode().value(),
          artistName);
      return Optional.empty();
    }

    // Depending on the fetch size in the client, we might get more results but we go with the first
    // in the array
    final List<String> ids = JsonPath.read(response.getBody(), "$.artists[:1].id");
    final Optional<String> id = ids.stream().findFirst();
    log.info("Got id '{}' for artist '{}'", id, artistName);
    return id;
  }

  public Optional<ArtistResult> fetchArtistInformationById(String mbid) {
    log.info("Contacting MusicBrainz about artist with id '{}'", mbid);
    final ResponseEntity<String> response = client.fetchArtistInformationById(mbid);

    if (!response.getStatusCode().is2xxSuccessful()) {
      log.warn(
          "MusicBraniz responded with '{}' for artist with id '{}'",
          response.getStatusCode().value(),
          mbid);
      return Optional.empty();
    }

    final Object document =
        Configuration.defaultConfiguration().jsonProvider().parse(response.getBody());

    final String name = JsonPath.read(document, "$.name");
    final Optional<String> wikidataId = extractWikidataId(document);
    final Optional<String> wikipediaTitle = extractWikipediaTitle(document);
    final List<AlbumResult> albumResults = extractAlbumResults(document);

    final ArtistResult artistResult =
        new ArtistResult(name, wikipediaTitle, wikidataId, albumResults);

    return Optional.of(artistResult);
  }

  // TODO Find example of wikipedia relation
  private static Optional<String> extractWikipediaTitle(final Object document) {
    return Optional.empty();
  }

  private static List<AlbumResult> extractAlbumResults(final Object document) {
    List<Map<String, String>> albums =
        JsonPath.read(document, "$['release-groups'][?(@['primary-type']=='Album')]");
    return albums.stream().map(MusicBrainzServiceImpl::convertToAlbumResult).toList();
  }

  private static AlbumResult convertToAlbumResult(final Map<String, String> album) {
    return new AlbumResult(album.get("id"), album.get("title"), album.get("first-release-date"));
  }

  // There should only be one wikidata link, but if there are more we'll go with the first found
  private static Optional<String> extractWikidataId(final Object document) {
    List<String> wikidataUrls =
        JsonPath.read(document, "$.relations[?(@.type=='wikidata')].url.resource");
    return wikidataUrls.stream().findFirst().map(MusicBrainzServiceImpl::extractWikidataId);
  }

  /* A wikidata link looks like this:
   * https://www.wikidata.org/wiki/Q15862
   *
   * Deliberately made package private for test access
   */
  static String extractWikidataId(final String wikidataUrl) {
    if (wikidataUrl == null) {
      return null;
    }
    String[] split = wikidataUrl.trim().split("/");
    if (split.length == 0) {
      return null;
    }

    String wikidataId = split[split.length - 1];
    if (wikidataId.isBlank()) {
      return null;
    }

    return wikidataId;
  }
}
