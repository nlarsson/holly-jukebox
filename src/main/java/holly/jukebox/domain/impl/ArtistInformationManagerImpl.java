package holly.jukebox.domain.impl;

import holly.jukebox.domain.ArtistInformationManager;
import holly.jukebox.domain.model.Album;
import holly.jukebox.domain.model.ArtistInformation;
import holly.jukebox.domain.model.ArtistInformation.ArtistInformationBuilder;
import holly.jukebox.service.coverartarchive.CoverArtArchiveService;
import holly.jukebox.service.musicbrainz.MusicBrainzService;
import holly.jukebox.service.musicbrainz.model.AlbumResult;
import holly.jukebox.service.musicbrainz.model.ArtistResult;
import holly.jukebox.service.wiki.WikiService;
import java.util.*;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fetches information about an artist.
 *
 * <p>This is the heart of the application that orchestrates how and when data is fetched.
 *
 * <p>This will attempt to provide as much information as possible. The underlying services might be
 * overwhelmed and at those times some data might be omitted. If no data can be extracted then the
 * response will result in an empty.
 */
@RequiredArgsConstructor
@Component
public class ArtistInformationManagerImpl implements ArtistInformationManager {
  private static final Logger log = LoggerFactory.getLogger(ArtistInformationManagerImpl.class);

  private final MusicBrainzService musicBrainzService;
  private final WikiService wikiService;
  private final CoverArtArchiveService coverArtArchiveService;

  @Override
  public ArtistInformation findArtistInformation(final String artistName) {
    log.info("Looking up artist information for artist '{}'", artistName);

    final ArtistInformationBuilder artistInformation = ArtistInformation.builder().name(artistName);

    final Optional<String> id = musicBrainzService.findIdByArtistName(artistName);

    // Can't proceed if we cannot find an id for the artist
    if (id.isEmpty()) {
      log.info("Could not find an id for artist '{}', skipping additional lookups", artistName);
      return artistInformation.build();
    }

    final String mbid = id.get();
    artistInformation.mbid(mbid);
    final Optional<ArtistResult> artistResultResponse =
        musicBrainzService.fetchArtistInformationById(mbid);

    if (artistResultResponse.isEmpty()) {
      log.info("Could not fetch arist information from MusicBrainz for id '{}'", mbid);
      return artistInformation.build();
    }

    final ArtistResult artistResult = artistResultResponse.get();
    artistInformation.name(artistResult.name());

    fetchWikipediaDescription(artistResult)
        .ifPresentOrElse(
            artistInformation::description,
            () -> log.info("No wikipedia description found for artist '{}'", artistResult.name()));

    final List<String> albumIds = artistResult.albums().stream().map(AlbumResult::id).toList();
    final Map<String, Optional<String>> frontCoverById =
        coverArtArchiveService.findFrontCoversForIds(albumIds);

    final List<Album> albums =
        artistResult.albums().stream().map(album -> convertToAlbum(album, frontCoverById)).toList();

    artistInformation.albums(albums);
    return artistInformation.build();
  }

  private static Album convertToAlbum(
      final AlbumResult album, final Map<String, Optional<String>> frontCoverById) {
    return Album.builder()
        .coverArtUrl(frontCoverById.get(album.id()).orElse(null))
        .id(album.id())
        .releaseDate(album.firstReleaseDate())
        .title(album.title())
        .build();
  }

  private Optional<String> fetchWikipediaDescription(final ArtistResult artistResult) {
    return extractWikipediaTitle(artistResult).flatMap(wikiService::fetchDescriptionForTitle);
  }

  private Optional<String> extractWikipediaTitle(final ArtistResult artistResult) {
    final Optional<String> wikipediaTitle = artistResult.wikipediaTitle();
    if (wikipediaTitle.isPresent()) {
      log.info("Wikipedia title found in artist result: '{}'", wikipediaTitle.get());
      return wikipediaTitle;
    }

    final Optional<String> wikidataId = artistResult.wikidataId();
    if (wikidataId.isPresent()) {
      Optional<Map<String, String>> sitelinkResponse =
          wikiService.fetchSitelinksForId(wikidataId.get());
      return sitelinkResponse.map(sitelinks -> sitelinks.get("enwiki"));
    }

    return Optional.empty();
  }
}
