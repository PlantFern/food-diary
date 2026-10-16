# Local development data

This directory is included in Flyway locations only when the `dev` Spring profile is active.

The repository intentionally does not track `V100__development_personal_data.sql` or the original dump because they contain user accounts and diary/profile data. To use the full local development dataset:

1. Save the generated `V100__development_personal_data.sql` file in this directory. The file is ignored by Git.
2. Ensure the database is a disposable local development database and is at a clean/current migration state before first use. Flyway applies V15 reference/catalog seed first, then V100 personal/demo data.
3. Start with the dev profile:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

The dump contains tables that are not part of the current application schema. Those legacy-only tables cannot be imported by the current V100 migration without their old table definitions and a deliberate schema-compatibility migration; retain the original dump locally if you need them.

The sample account password documented in `UserEntity.java` is `user_exemple_pass`; passwords are stored as BCrypt hashes.
