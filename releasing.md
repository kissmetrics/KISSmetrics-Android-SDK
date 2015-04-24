# Releasing

### 1. Make sure the tests run

An important step many may forget:

```sh
gradle connectedAndroidTest
```

You will need an Android emulator running to run the tests.

### 2. Bump Version Number

The version number needs to be increased.  If it is a feature addition/change
bump the second number (i.e. 2.X.0).  If it is a patch/bug fix bump the last
number (i.e. 2.0.X).

In the `build.gradle` file also bump the `versionCode` number by 1.  This is an
Android thing.

The version number lives in:

  1. `Connection.java` under `com.kissmetrics.sdk`.  It's in the constant `USER_AGENT`
  2. `build.gradle` file at the root.

### 3. Merge and Tag

Get your changes into the `master` branch.  Then create a tag with the version:

```sh
git tag -a vX.Y.Z -m 'Version X.Y.Z'
git push origin vX.Y.Z
```

Where `X.Y.Z` is your version number.

### 4. Build Artifacts

You can build everything you need with this command line and Gradle 2.3+:

```sh
gradle clean build compileReleaseSources javadocs javadocsJar sourcesJar
```

It will create the following files:

```
build/libs/KISSmetricsSDK-javadoc.jar
build/libs/KISSmetricsSDK-sources.jar
build/libs/KISSmetricsSDK.jar
build/outputs/aar/KISSmetricsSDK-debug.aar
build/outputs/aar/KISSmetricsSDK-release.aar
```

### 5. Release to Github

Create the Release on GitHub.  You have to compress all the files in Step 4 since
Github won't accept jar files.  Use the script `create-release-tar.sh` to build
the file.  It will appear as `build/KISSmetricsSDK.tar.bz`.