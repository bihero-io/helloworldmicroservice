# 'Hello World' microservice

Its purpose is to return 'Hello World' by http api (aggregated by hellomicroservice and worldmicroservice):
```bash
mvn clean package
java -jar target/helloworld-microservice-fat.jar
# using another terminal window
curl http://localhost:8080/ # Hello World
```