## Android Weather App

### Features

### Learning Outcome

- **Storing Secret Information:** For api calls we need api key. But this key should not be pushed to public repositories. Therefore, the right way to store and pass the apikey or any sensitive data during build time is adding it in `local.properties` in project root.
```properties
# local.properties
WEATHER_API_KEY=my-secret-key
```
Now to get this value during build time we add this in our module level `BuildConfig.java`. To do this open module level `build.gradle` add add these lines
```groovy
android {
    
    // other configurations here
    
    def localProperties = new Property()
    def localPropertiesFile = rootProject.file('local.properties')
    if (localPropertiesFile.exists()) {
        localPropertiesFile.withReader('UTF-8') {
            localProperties.load(it)
        }
    }
    
    buildFeature {
        buildConfig = true // must add otherwise show warning
    }
    
    buildConfigField( // adds new field to the module level BuildConfig.java
            "String", // field type
            "WEATHER_KEY_API", // field name
            "\"${localProperties.getPerperty('WEATHER_API_KEY')}\"" // field value, for string must add double quote
    )
}
```
Next build the project to create the BuildConfig file. Since local.properties and BuildConfig.java both are ignored during git push so the sensitive data are safe. 
**Note** make sure in `.gitignore` at project root directory and local.properties is added. 

- **API call using Retrofit2:** First of all I need to add the retrofit2 as well as one of the converters provided by Retrofit2 library. This converter is used serializing and deserializing the request and response. 
Like for this project gson converter is used so all the request body will be serialized to json and response body will be deserialized from json. During the retrofit build use `addConverterFactory(...)` method to add required converter.
- **Request URL:** Retrofit base url must end with `/` otherwise error occurs. Like this https://api.weatherapi.com/v1/. Also remember that if in service interface http method annotation, like GET POST etc., if `/` is prepended then
path will be considered relative the domain part. So don't prepend `/` in service method url paths. For example: if `@GET("/current.json")` then requests to https://api.weatherapi.com/current.json not to https://api.weatherapi.com/v1/current.json .
- **OkHttpClient Interceptor:** In cases when I need to something for each request, then we can use OkHttpClient `Interceptor` interface to manipulate each request before sending. For example: this is project we need to append a `key` query which
contains the api key value. I added an Interceptor that appends `key` query parameter to each request. 

### Change Log

- **v1.0**
 simple api call and display the current weather report for city **London** on success or log error body if request fails. 
 If any other exception occurs then a `Toast` message is displayed.