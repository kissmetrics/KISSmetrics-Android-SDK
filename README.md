# KISSmetrics Android SDK

This workspace is not the SDK. The KISSmetricsSDK project is intended to build the SDK as a jar.
The source files included here are not intended to be used directly in your app.

For implementation details please see: https://support.kissmetrics.io/reference#android

## Requirements

API level 8. Android SDK 2.2.X

## Using with Gradle

The KISSmetricsSDK jar is in the JCenter repo.  To include it add the following to
your `build.gradle`:

```groovy
dependencies {
  compile 'com.kissmetrics.sdk:KISSmetricsSDK:2.3.1'
}
```

If you don't already have `jcenter` in your `repositories` also include it:

```groovy
buildscript {
  repositories {
    jcenter()
  }
}
```

## File organization

Files are organized using the standard Android/Gradle file structure.  Code exists under
`src/main/java` and tests exist under `src/androidTest/java`.

## Building

You can build the library using the Gradle command line:

```sh
gradle build
```

It will create both a `jar` and an `aar` file under the build directory:


```
build/libs/KISSmetricsSDK.jar
build/outputs/aar/KISSmetricsSDK-debug.aar
build/outputs/aar/KISSmetricsSDK-release.aar
```

## Testing

Testing is standard Android style testing.  You need to be connected to either a Android Device
or Emulator since all the tests are run through the InstrumentationTestRunner.

You can run all tests through gradle:

```sh
gradle connectedAndroidTest --info
```

## Storing Data

Because our previously recommended open source SDK relied on Shared Preferences for storing the user
identity, so did we. For consistency we may want to someday migrate from Shared Prefrences to
Internal Storage. But there doesn't seem to be any pressing reason to do this now with the risk
being that we could disconnect all of our customer's Android users if the migration goes wrong. - WR

SETTINGS_FILE "KISSmetricsSettings" is used to persist data such as installUuid, doTrack, and cache
dates.

## Style

!!!: and ???: comments are used as types of TODOs in this project. To see them in the Task list in Eclipse goto preferences Java > Compiler > Task tags and add them.
(Show tasks: Window > Show View > Tasks)
