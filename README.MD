# Nordnet dev authenticator

Use this application to easily login to test environments at Nordnet. 
 - Test environment of the mobile app in IOS simulator 
 - Test environment of the mobile app in Android emulator (TODO)
 - Test environment of the web app next (nordnet-test.xy)
 - Local environment of the web app next (nordnet-local.xy:8081)

### Requirements for running
 - Java 17 and higher
 - jq (brew install jq)

### List of test users:
  List of customer (ids) that can be used for login exists as [properties](src/main/resources/application.yml) to this application.
  If you want to log in as other (test env) customers, just add them as properties and rerun the application.
        * (Recommended) Create a PR for the new users you add to the list, so that other developers can use them too. 
        * The name field is just a way to call each customer (id). They are not referencing to anything like username or ... .

### How to run the application (on MacOS and Linux)
    - Run `./gradlew bootRun` to start the application
    - `git pull` every now and again to get the latest features and customer(ids)

### Desktop shortcut:
    - `cd` to the project/repository root (important)
    - Run `./desktop-shortcut.sh`

## TODO
    - App Icon
    - Desktop shortcut icon
    - Add support for Android emulator
    - Add support for partner web
    - Automatically build the latest version of the app in github and send releases to company portal!
