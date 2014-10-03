# KISSmetrics Android SDK

For implementation details please see [our support docs](http://support.kissmetrics.com/apis/android)

## Getting Started

Insert information regarding how to get this library into your android
application.

## Minimum Requirements
  * API Level 7
  * Android SDK 2.1

## Development

All Android specific implementation details shall reside in [./sdk](./sdk).

Code must be able to compile under 1.6. There is no going around this for now.

Work on separate branches for features and submit pull requests.

This project will be IDE agnostic. We do not care which IDE you use to develop
on, we only ask that you do not commit IDE specific files. This means the
eclipse `.classpath` and `.project` along with IntelliJ's `.ipr`, `.iml`, and
`.iws`.

We are using gradle to help augment the development of the SDK. As such there
are plugins that allow support for specific IDE's. Currently the only IDE
defined is IntelliJ. If you want eclipse, a PR is welcome for the addition.

### Getting Started

Mac or Linux:

```sh
./gradlew idea
./gradlew compile
```

Windows

```sh
gradle.bat idea
gradle.bat compile
```

### Testing

We will be utilizing JUnit 4 as our testing framework. Please be sure to add
tests as you add features so that we can ensure no regressions are introduced.

We will be setting up a CI server to ensure that all Pull Requests meet
expectations.

## Storing Data:

Because our previously recommended open source SDK relied on Shared Preferences
for storing the user identity, so did we. For consistency we may want to someday
migrate from Shared Prefrences to Internal Storage. But there doesn't seem to be
any pressing reason to do this now with the risk being that we could disconnect
all of our customer's Android users if the migration goes wrong. - WR

## Style

!!!: and ???: comments are used as types of TODOs in this project. To see them
in the Task list in Eclipse goto preferences Java > Compiler > Task tags and add
them. (Show tasks: Window > Show View > Tasks)
