DataStore Delegates
============
Property delegation and extensions for Android's `DataStore<Preferences>`s, including:
* Read/Write variable delegation
* Flow and SharedFlow extensions
* Preference type conversions


Use
-------
The latest version can be found at [Maven Central][MavenCentral].

```gradle
repositories {
  mavenCentral()
}

dependencies {
  implementation 'com.devbrackets.android:datastore:0.0.1-preview01'
}
```


Quick Start
-------
```kotlin
// Supported Preference type
var userColor by dataStore.value("userColor", "0xFFFFFF")
val userColorFlow = dataStore.flow("userColor", "0xFFFFFF")

// Preference with conversion
var userColor by dataStore.value("userColor", Color.valueOf(0xFFFFFFFF), ColorValueConverter())
val userColorFlow = dataStore.flow("userColor", Color.valueOf(0xFFFFFFFF), ColorValueConverter())
```


License
-------
    Copyright 2023 DataStore Delegates Contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


Attribution
-----------
* Uses [Kotlin](https://kotlinlang.org/) licensed under [Apache 2.0][Apache 2.0]
* Uses [DataStore](https://developer.android.com/jetpack/androidx/releases/datastore) licensed under [Apache 2.0][Apache 2.0]

 [Apache 2.0]: http://www.apache.org/licenses/LICENSE-2.0
 [MavenCentral]: https://s01.oss.sonatype.org/#nexus-search;quick~com.devbrackets.android.datastore
