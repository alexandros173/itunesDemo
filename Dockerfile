
 FROM openjdk:8
 

 VOLUME /tmp
 

 ADD ./target/itunesdemo-0.0.1-SNAPSHOT.jar /itunesdemo.jar
 
 
 RUN sh -c 'touch /itunesdemo.jar'
 

 ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/itunesDemo.jar"]