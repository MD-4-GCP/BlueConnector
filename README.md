This service provides a basic API to call SAP functions

# Build

## Download and add SAP JCo Libs

## Maven Build
The only thing you need is to run `mvn clean install`. The rest is handled by Maven. 




# Configure

## Create Destination
In order to successfully run against an existing SAP system you have to update the destination with the current IP of the system (in my case, because my SAP system is provisioned by SAP CAL). Destinations are stored in the destinations folder locally (and they will be packed into the docker image. Alternatively, as soon as the service is running, you can update the destination via the API. 

## Destination Storage Sync
The service also provides sync of destinations with Google Cloud Storage. If multiple instances of the service are running in parallel they need to have access to the same destination files. Therefore you need to keep the following things in mind:

1. Authentication of Service against GCS
    - If the service is running on App Engine, the service account is automatically provisioned and you don't need to deal with auth
    - If the service runs on your own docker or locally then you need to provide the credentials of a service account <https://cloud.google.com/docs/authentication/getting-started>

2. Enter Bucket Name
    - The Dockerfile contains an ENV (GCS_STORAGE_BUCKET) that sets the name of the bucket to be used. This bucket needs to exists and needs to have a folder called destinations.  



# Run

## Run on Docker 
You can run this on Docker by simply building and running it

```
docker build --tag blueconnector .
docker run -p 8080:8080 blueconnector
```

## Run on App Engine Flex
Or you can run it in App Engine Flex (custom runtime)

```
gcloud app deploy
```




# Use

   Documentation is currently under creation



