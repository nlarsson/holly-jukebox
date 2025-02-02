package holly.jukebox.service.musicbrainz.impl;

import holly.jukebox.service.musicbrainz.MusicBrainzCache;
import holly.jukebox.service.musicbrainz.MusicBrainzRestClient;
import holly.jukebox.service.musicbrainz.config.MusicBrainzConfig;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class MusicBrainzRestClientImpl implements MusicBrainzRestClient {
  private static final Logger log = LoggerFactory.getLogger(MusicBrainzRestClientImpl.class);

  private final RestClient client;
  private final MusicBrainzCache musicBrainzCache;

  public MusicBrainzRestClientImpl(
      final RestClient.Builder clientBuilder,
      final MusicBrainzCache musicBrainzCache,
      final MusicBrainzConfig musicBrainzConfig) {
    // Added custom user agent for identification as per requested at
    // https://musicbrainz.org/doc/MusicBrainz_API/Rate_Limiting#Provide_meaningful_User-Agent_strings
    String baseUrl = musicBrainzConfig.baseUrl();
    String userAgent = musicBrainzConfig.userAgent();
    client = clientBuilder.baseUrl(baseUrl).defaultHeader("user-agent", userAgent).build();
    this.musicBrainzCache = musicBrainzCache;
  }

  @Override
  public ResponseEntity<String> findArtistByName(final String name) {
    final Optional<String> cachedArtistResponse = musicBrainzCache.findArtistByName(name);

    if (cachedArtistResponse.isPresent()) {
      log.info("Artist '{}' found in cache", name);
      return ResponseEntity.ok(cachedArtistResponse.get());
    }

    log.info("Outgoing call to MusicBrainz for artist '{}'", name);
    final ResponseEntity<String> response =
        client
            .get()
            .uri("/artist/?query=artist:{name}&fmt=json&limit=1", Map.of("name", name))
            .retrieve()
            .toEntity(String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      musicBrainzCache.storeArtistByNameResponse(name, response.getBody());
    }

    return response;
  }

  @Override
  public ResponseEntity<String> fetchArtistInformationById(final String mbid) {
    final Optional<String> cachedArtistInformationResponse =
        musicBrainzCache.fetchArtistInformationById(mbid);

    if (cachedArtistInformationResponse.isPresent()) {
      log.info("Artist information for artist with id '{}' found in cache", mbid);
      return ResponseEntity.ok(cachedArtistInformationResponse.get());
    }

    log.info("Outgoing call to MusicBrainz for artist with id '{}'", mbid);
    final ResponseEntity<String> response =
        client
            .get()
            .uri("/artist/{mbid}?&fmt=json&inc=url-rels+release-groups", Map.of("mbid", mbid))
            .retrieve()
            .toEntity(String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      musicBrainzCache.storeArtistInformationByIdResponse(mbid, response.getBody());
    }

    return response;
  }
}
