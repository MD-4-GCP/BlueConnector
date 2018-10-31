FROM gcr.io/google-appengine/openjdk
EXPOSE 8080

ADD target/blueblueapi-jar-with-dependencies.jar /app.jar
ADD libs/ /libs/

#Option 1) Copy the local destination files
ADD destinations/ /destinations/ 

#Option 2) Sync with GCS bucket
ENV GCS_STORAGE_BUCKET=blueconnector

ENTRYPOINT exec java -cp /app.jar:/libs/sapjco3.jar de.dietzm.blueblue.ServiceStarter