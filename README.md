# DroidFloatingSearchView
Yet another floating search view library for android

<b><h1>About</h1></b>
An easy to implement floating search view with customizations.

<b>Supports from Android SDK version 19 and above.</b><br/><br/>
<b>Requires androidx.</b>

<p align="center">
<img src="https://user-images.githubusercontent.com/12429051/32132439-1cf9499c-bbe1-11e7-8da0-7bcf90485afc.png" height="550" width="275"/>
<img src="https://user-images.githubusercontent.com/12429051/32132378-5fb91baa-bbe0-11e7-8f94-f1fa7a0ef634.png" height="550" width="275"/>
<img src="https://user-images.githubusercontent.com/12429051/32132440-1d327f3c-bbe1-11e7-9811-5e28e9388760.png" height="550" width="275"/>
</p>

<b><h1>Usage</h1></b>
<b>Gradle dependency:</b>

Add the following to your project level build.gradle:

```java
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Add this to your app build.gradle:

```java
dependencies {
    implementation 'com.github.vikramezhil:DroidFloatingSearchView:v2.0.1'
}
```

Add the following to the <repositories> section of your pom.xml:

```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Add the following to the <dependencies> section of your pom.xml:

```xml
<dependency>
    <groupId>com.github.vikramezhil</groupId>
    <artifactId>DroidFloatingSearchView</artifactId>
    <version>v2.0.1</version>
</dependency>
```

<b><h1>Documentation</h1></b>

For a detailed documentation 📔, please have a look at the [Wiki](https://github.com/vikramezhil/DroidFloatingSearchView/wiki).

In your layout file add Droid Floating Search View,

```xml
<github.com.vikramezhil.dfsv.Dfsv
    android:id="@+id/dfsView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:dfsvBgColor="@android:color/holo_purple"
    app:dfsvOverlayBgColor="@android:color/holo_purple"
    app:dfsvTextColor="@android:color/white"
    app:dfsvHintTextColor="@android:color/white"
    app:dfsvDividerColor="@android:color/white"
    app:dfsvIconsColor="@android:color/white"
    app:dfsvHintText="@string/search_hint"
    app:dfsvCornerRadius="10"
    app:dfsvElevation="5"
    app:dfsvCloseSearchOnOverlayTouch="false"
    app:dfsvContinuousSearch="false"
    app:dfsvSuggestions="@array/suggestions"/>
```

In your class file, initialize Droid Floating Search View using the ID specified in your layout file

```java
DroidFSView dfsView = findViewById(R.id.dfsView);
```

Set and implement the Droid Floating Search View listener methods

```java
dfsView.setOnDroidFSViewListener(new OnDroidFSViewListener() {
    @Override
    public void onHasFocus() {
        Log.i(TAG, "Has focus");
    }

    @Override
    public void onLostFocus() {
        Log.i(TAG, "Lost focus");
    }

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        Log.i(TAG, "Old query = " + oldQuery + ", New Query = " + newQuery);
    }

    @Override
    public void onSearchText(String searchQuery) {
        Log.i(TAG, "Query to search = " + searchQuery);
    }

    @Override
    public void onActionItemClicked() {
        Log.i(TAG, "Action item clicked");
    }
});
```