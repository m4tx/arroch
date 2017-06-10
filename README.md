Arroch
======


## IntelliJ IDEA quickstart
* Requirements: 
  * Oracle JDK (OpenJDK is not supported)
  * IntelliJ IDEA Ultimate with Scala plugin
* Steps:
  * Import project from Git
  * Go to `build.sbt` and import SBT project
  * Wait until all dependencies are resolved and project indexed
  * In any class file in `app` choose `Run Play 2 App` from the context menu

## Data Scrapers
### Google People API
In order to make it working:
1. Visit the [Google Developer Console](https://console.developers.google.com/)
2. Create new project
3. Enter API library and enable "Google People API"
4. Enter credentials tab
5. Click Create credentials ➜ OAuth Client ID ➜ Web Application
6. In the form that showed up, enter the redirect URL: `http://localhost:9000/google/authenticated/` and click Create
7. Enter the newly created client ID and click "Download JSON"; put the file in `conf/google_client_secrets.json`
8. Visit http://localhost:9000/google and it should, after consent, import the People into the database

### Facebook
1. Login to https://mbasic.facebook.com/home.php
2. Extract Facebook cookies from browser and put them in file `conf/facebook_cookies`
3. Do not logout
