package holly.jukebox.service.coverartarchive.config;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "cover-art-archive")
public record CoverArtArchiveConfig(
    @DefaultValue("http://coverartarchive.org") String baseUrl,
    @DefaultValue RequestTimeout requestTimeout) {
  public record RequestTimeout(
      @DefaultValue("5") long timeout, @DefaultValue("SECONDS") TimeUnit timeUnit) {}
}
