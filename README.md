KISSmetrics-Android-SDK
=======================

This workspace is not the SDK. The KISSmetricsSDK project is intended to build the SDK as a jar.
The source files included here are not intended to be used directly in your app.

For implementation details please see: http://support.kissmetrics.com/apis/android


Requirements:
-------------
API level 7. Android SDK 2.1


File organization:
------------------
All SDK source files live in the KISSmetricsAPI directory.


Environment and Eclipse Settings:
---------------------------------
We're using the 1.6 compiler. See notes in 'Testing' section.


Testing:
--------
We're using JUnit4 for unit tests and the android.test framework for integration tests.
Any tests dealing with disk IO must be part of the integration tests.

The `Android Support Repository` package must be installed via the Android SDK Manager to run tests.

Storing Data:
-------------
Because our previously recommended open source SDK relied on Shared Preferences for storing the user identity, so did we. For consistency we may want to someday migrate from Shared Prefrences to Internal Storage. But there doesn't seem to be any pressing reason to do this now with the risk being that we could disconnect all of our customer's Android users if the migration goes wrong. - WR

SETTINGS_FILE "KISSmetricsSettings" is used to persist data such as installUuid, doTrack, and cache dates.


Style:
-------
!!!: and ???: comments are used as types of TODOs in this project. To see them in the Task list in Eclipse goto preferences Java > Compiler > Task tags and add them.
(Show tasks: Window > Show View > Tasks)


