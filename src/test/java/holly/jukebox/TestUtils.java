package holly.jukebox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

  public static String musicBrainzResource(final String resourceName) throws IOException {
    return readFile("src/test/resources/music-brainz/" + resourceName);
  }

  public static String wikiResource(final String resourceName) throws IOException {
    return readFile("src/test/resources/wiki/" + resourceName);
  }

  public static String coverArtArchiveFile(final String resourceName) throws IOException {
    return readFile("src/test/resources/cover-art-archive/" + resourceName);
  }

  public static String readFile(final String resourceName) throws IOException {
    Path path = Paths.get(resourceName);
    return Files.readString(path);
  }
}
