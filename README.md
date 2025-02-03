[![CI](https://github.com/nlarsson/holly-jukebox/actions/workflows/ci.yml/badge.svg)](https://github.com/nlarsson/holly-jukebox/actions/workflows/ci.yml)

# Holly Jukebox

RESTful API to fetch information about music artists

## Get started

For local development you can simply run `./mvnw springboot:run`.
By default the port used is 5050.

The api exposes a single endpoint:
/api/v1/search/<artist name>

eg: `curl http://localhost:5050/api/v1/search/queen`

Make sure to URL encode the search parameter, mostly this means to replace space with %20. Eg:
`/api/v1/search/system%20of%20a%20down`

The application is very forgiving and will in most cases try to return something.
This might not be ideal depending on the intended consumer(s) of this REST API, for instance a 404 could be returned
when an ID from MusizBrainz cannot be found.

## Configuration

See [application.example.yml](./src/main/resources/application.example.yml) for information about what custom
configuration is available.

## Maven

This project utilizes [Maven Wrapper](https://maven.apache.org/wrapper/) to make sure the same version of Maven is used
at all places.

Spotless has been configured to unify formatting.
This is the source of truth for how files are to be formatted.
You can run `./mvnw spotless:check` to see if the project is ok - this is a nice goal to trigger in CI/CD pipelines.
Alternatively, you can run `/mvnw spotless:apply` to fix all formatting problems.

## Shortcomings

### Error handling

More error handling can be done to make the application more robust.
Such as handling thrown exceptions when doing JsonPath extraction - now such exceptions will bubble up and cause
internal server errors.
Actions that could trigger such exceptions would be searches where certain attributes are missing.

If a request towards an external API fails we don't try again, or inform the user know, the application accepts the
outcome and continues with its duties.

### Cover art fetching

If an artist has been in the game for a long time they might have racked up quite a large amount of albums.
Fetching all of them at almost exactly the same time might degrade the external system.
By adding some jitter to the calls might actually improve the overall speed for this application.

### Testing

Integration testing for the whole application would be nice.
Only having unit tests opens up for the possibility that all individual units works as intended but the plumping has
done wrong somewhere.

There are still some key places that needs unit tests.

### Caching

Caching has been setup in the more simple way possible. While it's unlikely that artist information changes often, a
completely stale cache is not good.
However, this has been left as proof-of-concept and can quite easily be replaced with a more suitable implementation.

### Configuration

A lot of things are hardcoded, only the most necessary has been configured to allow for local testing.
