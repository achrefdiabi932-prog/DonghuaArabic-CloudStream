# DonghuaArabic for CSWeb TV

A CloudStream-compatible extension project for the Arabic DonghuaArabic website.

- Website: https://www.donghuarabic.com/
- Target app: CSWeb TV on LG webOS (CloudStream extension runtime)
- Extension module: `DonghuaArabicProvider`
- Repository manifest: `builds/repo.json`

## Current status

This is an **early development build**, not a finished player.

The provider draft attempts to read catalogue cards, search results, and title metadata from the public website. The live HTML selectors must be checked and adjusted. Playback extraction is intentionally not implemented yet.

## Build

GitHub Actions builds the `.cs3` package and publishes `plugins.json` plus `repo.json` to the `builds` branch.

After a successful build, add this repository URL in CSWeb TV:

```
https://raw.githubusercontent.com/achrefdiabi932-prog/DonghuaArabic-CloudStream/builds/repo.json
```

Adding the repository only makes the extension available in the app; it does not automatically install it.
