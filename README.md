# Ordika - Backend spring boot application for the Orang3 app

## Running locally
### To run dynamodb-local docker container:
1) navigate to src/main/resources/db
2) run docker-compose up
3) dynamodb-local will be running in docker container on port 8000

### To run Ordika locally (not using docker)
#### Ensure .env-dev.properties is present in the root of the project
1. Add below to cli arguments in your run configuration 
- --spring.profiles.active=dev,local --spring.config.additional-location=file:.env-dev.properties
- NOTE: local profile is needed to run the uat environment locally => uat,local
- NOTE: to run uat environment locally, we need access key id and secret access key with the permissions to modify 
uat dynamodb and s3 dev buckets.

### To run Ordika in docker container:
#### Build image
1) docker build -t ordika-image:latest .

#### Need to run Ordika container in same network (db_default) created by running dynamodb-local image. Otherwise, Ordika cannot reach dynamodb-local on port 8000 since they are on different networks, and error 500 will be returned.
2) docker run -d -p 8080:8080 --rm --name ordika --network db_default ordika-image

#### NOTE: the hostname in application-local.yml is dynamodb-local, which is either the services name or container name in application-local.yml  
#### NOTE: ordika and dynamodb-local containers need to be removed before we can run docker-compose down for dynamodb-local, since we had to attach ordika container to the network db_default. 