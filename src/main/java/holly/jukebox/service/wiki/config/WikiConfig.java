package holly.jukebox.service.wiki.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "wiki")
public record WikiConfig(@DefaultValue Wikidata wikidata, @DefaultValue Wikipedia wikipedia) {
  public record Wikidata(@DefaultValue("https://www.wikidata.org/w/api.php") String baseUrl) {}

  public record Wikipedia(@DefaultValue("https://en.wikipedia.org/w/api.php") String baseUrl) {}
}
