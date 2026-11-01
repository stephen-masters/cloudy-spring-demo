# Security policy

## Reporting a vulnerability

Please report suspected vulnerabilities privately through GitHub's
[private vulnerability reporting](https://github.com/stephen-masters/cloudy-spring-demo/security/advisories/new)
(Security tab → "Report a vulnerability"). Do not open a public issue for a
security problem.

## Supported versions

This is a demonstration project with no releases. Only `main` is maintained.

## What is done to keep it safe

- Dependency versions come from the Spring Boot parent and the Spring Cloud
  BOM, kept current by Dependabot, which also keeps the Maven wrapper and the
  workflow actions current. The few Eureka dependencies that neither
  manages are pinned in the parent `pom.xml`.
- Trivy scans the dependencies of every module, through the aggregate
  CycloneDX SBOM, for HIGH and CRITICAL vulnerabilities on every push and
  pull request, and weekly on a schedule, and CI publishes the SBOMs.
- The services are a demonstration, not something to deploy: they have no
  authentication, the APIs allow cross-origin requests from anywhere, and
  the crypto API logs the plaintext it is given. The language API's
  database is an in-memory HSQLDB unless the `localdb` profile is chosen.
