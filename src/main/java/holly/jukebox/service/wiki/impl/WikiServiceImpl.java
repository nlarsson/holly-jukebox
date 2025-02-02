package holly.jukebox.service.wiki.impl;

import com.jayway.jsonpath.JsonPath;
import holly.jukebox.service.wiki.WikiService;
import holly.jukebox.service.wiki.WikidataRestClient;
import holly.jukebox.service.wiki.WikipediaRestClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WikiServiceImpl implements WikiService {
  private static final Logger log = LoggerFactory.getLogger(WikiServiceImpl.class);

  private final WikidataRestClient wikidataRestClient;
  private final WikipediaRestClient wikipediaRestClient;

  @Override
  public Optional<Map<String, String>> fetchSitelinksForId(final String wikidataId) {
    log.info("Contacting Wikidata for site links for id '{}'", wikidataId);
    final ResponseEntity<String> response = wikidataRestClient.fetchSitelinksForId(wikidataId);

    if (!response.getStatusCode().is2xxSuccessful()) {
      log.warn(
          "Wikidata responded with '{}' for id '{}'", response.getStatusCode().value(), wikidataId);
      return Optional.empty();
    }

    final List<Map<String, String>> sitelinks = JsonPath.read(response.getBody(), "$..sitelinks.*");
    final Map<String, String> titleBySiteMapping =
        sitelinks.stream().collect(Collectors.toMap(key("site"), key("title")));

    return Optional.of(titleBySiteMapping);
  }

  @Override
  public Optional<String> fetchDescriptionForTitle(final String wikipediaTitle) {
    log.info("Contacting Wikipedia for description of title '{}'", wikipediaTitle);
    final ResponseEntity<String> response =
        wikipediaRestClient.fetchDescriptionForTitle(wikipediaTitle);

    if (!response.getStatusCode().is2xxSuccessful()) {
      log.warn(
          "Wikipedia responded with '{}' for title '{}'",
          response.getStatusCode().value(),
          wikipediaTitle);
      return Optional.empty();
    }

    final List<String> description = JsonPath.read(response.getBody(), "$..extract");
    return description.stream().findFirst();
  }

  private static Function<Map<String, String>, String> key(final String key) {
    return map -> map.get(key);
  }
}
