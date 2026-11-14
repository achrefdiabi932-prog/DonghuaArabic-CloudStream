# DonghuaArabic CloudStream extension

A cross-platform CloudStream provider for the Arabic DonghuaArabic catalogue.

- Website: https://www.donghuarabic.com/
- Targets: CloudStream on Android and CSWeb TV on LG webOS (where the app supports CloudStream plugins)
- Extension module: `DonghuaArabicProvider`
- Published repository manifest: `builds/repo.json`

## Development status

This is an early development build, not yet a finished streaming extension.

The provider currently contains a defensive first pass for catalogue cards, search, and title metadata. Website selectors still need validation against current live pages. Episode/stream extraction is not implemented, so the extension cannot play episodes yet. No DRM or access-control bypass is attempted.

## Build and publishing

GitHub Actions builds the CloudStream `.cs3` plugin and publishes the generated plugin list and repository manifest to the `builds` branch after pushes to `main`.

Pull requests run the build but do not publish artifacts. The workflow installs the Android SDK platform/build tools required by the Android Gradle plugin and uses Gradle 8.9 (the repository does not include a Gradle wrapper).

## Install in CSWeb TV

After a successful workflow run, add this repository URL in CSWeb TV:

```
https://raw.githubusercontent.com/achrefdiabi932-prog/DonghuaArabic-CloudStream/builds/repo.json
```

Then install the DonghuaArabic plugin from the repository inside the app. Adding the repository only makes the extension available; it does not automatically install it.

## Android

The same `.cs3` package can be used by compatible CloudStream Android builds. Add the published repository URL in the app's extension/repository settings and install the provider.
