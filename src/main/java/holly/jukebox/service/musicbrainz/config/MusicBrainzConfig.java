package holly.jukebox.service.musicbrainz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "music-brainz")
public record MusicBrainzConfig(
    @DefaultValue("https://musicbrainz.org/ws/2") String baseUrl,
    @DefaultValue("HollyJukebox/0.0.1 nlarsson@hotmail.se") String userAgent) {}
