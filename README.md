# Image scaling service
This is a media service written using Spring Boot, H2 Database and the 
File System for storing media. Utilizes HTTP as a transport 
for uploading and storing images. Images uploaded are resized into 
thumbnail sizes, thumbnail x2 sizes, medium sizes and each variant is stored.
## Usage
It is bootstrapped with the gradle build tool. From within the project directory, run:
```shell script
./gradlew bootRun
```
## Running Tests
In order to run test suite, run:
```shell script
./gradlew test
```
## Documentation
Documentation for Rest API lives [here](https://benjamincath.gitbook.io/image-scaling-service/) &nbsp; 🚀