# AGENTS.md

Guidance for anyone working in this repository with a coding agent (Claude
Code, Codex, Cursor, Gemini CLI or whatever else reads this file) and a fair
summary of the house rules for contributors who use none.

## What this repository is

**cloudy-spring-demo** is a small demonstration of service discovery with
Spring Cloud Netflix Eureka. It is a teaching example, not a library or an
application: nothing is released or deployed, and the code should stay as
short as the point it makes.

The point is this. A web application (`csd-web`) calls an API by its
service name, `http://language-api/languages`, through a `@LoadBalanced`
`RestTemplate`. Spring Cloud LoadBalancer asks the Eureka client for the
instances registered under that name and sends the request to one of them.
Neither side is configured with the other's address; both know only where
Eureka is.

## Hard rules

### Discovery is the demo

Do not replace the service names in URLs with host names or ports, and do
not configure fixed instances outside the tests. A change that makes the
web application work without Eureka has removed the thing being shown.

### The integration test is the demo's claim

`csd-integration-tests` starts the Eureka server, both APIs and the web
application in one JVM, waits for the APIs to register, and checks that the
web page lists the languages the language API returns. Do not weaken it, or
replace the waiting with fixed sleeps.

### One build, one parent

Every module is a child of the parent `pom.xml`, which owns the Spring Boot
parent, the Spring Cloud BOM, the plugins and the enforcer rules. Modules
declare dependencies without versions. Executable jars carry the `exec`
classifier so that the plain jars can go on the integration tests'
classpath.

### Stay on current versions

The project tracks the latest LTS JDK (`java.version` in `pom.xml`, and
`java-version` in the workflows; change them together), the latest Spring
Boot GA release, and the Spring Cloud release train that the
[compatibility table](https://spring.io/projects/spring-cloud#overview)
lists for it (`spring-cloud.version`). Do not pin a version that Spring Boot
or Spring Cloud already manages. The pins in the parent's
`dependencyManagement` exist only because Eureka's own dependencies do not
converge; drop each one once the enforcer passes without it.

## Layout

| Path | What |
|---|---|
| `csd-eureka/` | The Eureka server (`@EnableEurekaServer`) |
| `csd-language-api/` | Languages from an HSQLDB table loaded by `import.sql`; registers as `language-api` |
| `csd-crypto-api/` | BCrypt hashing and checking; registers as `crypto-api` |
| `csd-web/` | The Thymeleaf page; registers as `api-client` and calls `language-api` |
| `csd-integration-tests/` | All four services in one JVM; tests only |
| `*/config/application.yml` | Log file location when a service is run from its module directory |
| `.githooks/commit-msg` | Conventional Commits check, shared from `scattercode/release-pipelines` |

Each module's unit tests switch the Eureka client off. `csd-web`'s test
puts Spring Cloud's simple discovery client in its place, so the
load-balanced `RestTemplate` still resolves `language-api` to an instance.

## Checks

```bash
./mvnw spotless:apply   # format the Java sources and the POMs
./mvnw verify           # everything below, and the tests
```

`./mvnw verify` runs, in each module:

- the **enforcer**: the JDK is at least `java.version`, Maven is at least
  3.9, and dependency versions converge;
- the **Spotless** check (palantir-java-format for Java, sortpom for each
  `pom.xml`) in the `validate` phase;
- the **compiler** with `-Xlint:all -Werror`, so any warning fails the build;
- the **tests**.

It needs JDK 25; the Maven wrapper fetches Maven itself. All of it must
pass before a commit. The lefthook hooks run the format check before each
commit and the full build before each push; CI runs the build and a Trivy
scan on every push and pull request. Trivy scans the parent's aggregate
SBOM, `target/classes/META-INF/sbom/application.cdx.json`, which covers
every module.

## Conventions

- British English; sentence case for headings.
- Conventional Commits, enforced by `.githooks/commit-msg` and, for pull
  request titles, by `pr-title.yml`. Dependency updates are `build:`,
  workflow changes `ci:`.
- `.githooks/commit-msg` is a byte-for-byte copy of the template in
  `scattercode/release-pipelines`; CI checks it has not drifted. Change it
  there, not here.
- `main` is protected: changes arrive through pull requests with the CI
  checks passing.
- Hook setup in a fresh clone: `lefthook install`, or without lefthook,
  `git config core.hooksPath .githooks` for the commit message check alone.
