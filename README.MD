# Nordnet dev authenticator

Use this application to easily login to test environments at Nordnet. 
 - Test environment of the mobile app in IOS emulator 
 - Test environment of the mobile app in Android simulator (TODO)
 - Test environment of the web app next (nordnet-test.xy)
 - Local environment of the web app next (nordnet-local.xy:8081)

### Requirements for running
 - Java 17 and higher
 - jq (brew install jq)
 - 

### List of test users:
  List of customer (ids) that can be used for login exists as [properties](src/main/resources/application.yml) to this application.
  If you want to log in as other (test env) customers, just add them as properties and rerun the application.
        * (Recommended) Create a PR for the new users you add to the list, so that other developers can use them too. 
        * The name field is just a way to call each customer (id). They are not referencing to anything like username or ... .

#### First use:
    - Run the script called `desktop-icon.sh` to create a desktop icon for the script that runs the application!
    - Run the application by clicking the icon on the desktop

#### Subsequent uses:
    - Run the application by clicking the icon on the desktop
    - `git pull` every now and again to get the latest features and customer(ids)
