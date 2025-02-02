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
/api/v1/search/system%20of%20a%20down

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