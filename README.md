Cloudy Spring demo
==================

A proof of concept of a web application calling a couple of APIs that it finds through Eureka service
discovery, using Spring Cloud Netflix. There are four services, each a Spring Boot application in its own
module of one Maven build:

| Module | Registers as | Port | What it does |
|---|---|---|---|
| `csd-eureka` | (the registry) | 8761 | Eureka server; the dashboard is at <http://localhost:8761/> |
| `csd-language-api` | `language-api` | 8080 | `GET /languages` and `GET /languages/{isocode}`, from an in-memory HSQLDB |
| `csd-crypto-api` | `crypto-api` | 50002 | `/bcrypt/{plaintext}`, `/bcrypt?plaintext=…&salt=…` and `/bcryptcheck`: BCrypt hashing |
| `csd-web` | `api-client` | 10080 | A Thymeleaf page listing the languages it fetches from `http://language-api/languages` |

The web application never knows where the language API runs. Its `RestTemplate` is marked
`@LoadBalanced`, so Spring Cloud LoadBalancer resolves the host name `language-api` to whichever
instance Eureka has registered under that name. `csd-integration-tests` starts all four in one JVM and
checks exactly that.

It needs Java 25 and uses Spring Boot 4.1 with Spring Cloud 2025.1. The Maven wrapper downloads Maven
itself, so to run a full build, including the format check and the tests:

    ./mvnw clean verify

To run the demo, start each service in its own terminal, Eureka first, then open <http://localhost:10080/>
once the APIs show up on the Eureka dashboard (it takes up to half a minute):

    ./mvnw -pl csd-eureka spring-boot:run
    ./mvnw -pl csd-language-api spring-boot:run
    ./mvnw -pl csd-crypto-api spring-boot:run
    ./mvnw -pl csd-web spring-boot:run

The Eureka server, the crypto API and the web application take their port from the `PORT` environment
variable if it is set; the language API takes `SERVER_PORT`. The language API's `localdb` profile
reads from a local MySQL database instead of HSQLDB.

If the format check fails, `./mvnw spotless:apply` fixes it. To install the git hooks, which check
formatting before each commit and run the build before each push:

    lefthook install

See [AGENTS.md](AGENTS.md) for the conventions and [SECURITY.md](SECURITY.md) for reporting a
vulnerability.

Licensed under the [MIT License](LICENSE): copy and adapt it freely.
