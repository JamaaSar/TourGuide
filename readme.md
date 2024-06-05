
# TourGuide :

# Technologies

> Java 21
> Apache Maven 3.9.4
> Spring Boot 3.2.4
> Maven dependencies : (Lombok /Jacoco )


# Getting Started :
### Clone or download

> git clone git@github.com:JamaaSar/TourGuide.git


### Use Maven to build application

> mvn clean install

# How to have gpsUtil, rewardCentral and tripPricer dependencies available ?

> Run : 
- mvn install:install-file -Dfile=./libs/gpsUtil.jar -DgroupId=gpsUtil 
  -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=./libs/RewardCentral.jar -DgroupId=rewardCentral 
  -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=./libs/TripPricer.jar -DgroupId=tripPricer 
  -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar


