# Configuration Management Guide

This document describes the software configuration management (SCM) practices
applied in this repository, as required by ISYS3001 Assessment 2.

## 1. Version Control

- Tool: Git, hosted on GitHub.
- Every artifact that changes over time is version controlled: source code,
  build scripts, configuration files, documentation and the changelog.
- Generated artifacts (`target/`, IDE files) are excluded via `.gitignore`.

## 2. Branching Strategy

A lightweight feature-branch workflow is used:

```
main ────────────────┬──────────┬───────────►  stable, releasable
   \                 /          /
    feature/setup-project      /
                 \             /
                  feature/domain-model
                          \
                           feature/ordering-api  ...
```

- `main` always contains a working version of the application.
- Each feature or fix is developed on its own branch
  (`feature/<short-name>`), then merged back into `main` with
  `--no-ff` so every feature remains visible in the history.
- Configuration changes (e.g. new properties) go through the same process.

## 3. Commit Conventions

- Commit messages are written in English, in the imperative mood:
  `Add order deadline validation`, `Fix stock return on cancellation`.
- The first line stays under 72 characters; the body (when needed) explains
  *why* the change was made.
- `CHANGELOG.md` summarizes every release.

## 4. Application Configuration Management

- All environment-specific values (database URL, credentials, port) are
  externalized in `application.properties` through
  `${ENV_VAR:default}` placeholders.
- Deployment targets override configuration without rebuilding:
  the same JAR runs in development, test and production.
- `application-prod.properties` demonstrates a dedicated production profile
  (activated with `--spring.profiles.active=prod`).
- Secrets are never committed; the repository contains only harmless local
  defaults intended for development.

## 5. Build and Deployment Configuration

- Maven (`pom.xml`) pins the Java version (1.8) and all dependency versions,
  so builds are reproducible.
- The Spring Boot Maven plugin produces an executable JAR:
  `mvn clean package` → `java -jar target/food-coop-ordering-system-1.0.0.jar`.
- Database schema evolution is handled by Hibernate `ddl-auto=update`;
  the reference schema in `src/main/resources/db/schema.sql` documents the
  expected structure for manual provisioning.
- Seed data in `data.sql` is idempotent so repeated deployments are safe.

## 6. Release Baselines

Each merge into `main` is tagged with a semantic version
(`v1.0.0` for the first release), giving a stable baseline that can be
redeployed or rolled back at any time.
