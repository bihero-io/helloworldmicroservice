# 'Hello World' microservice

Its purpose is to return 'Hello World' by http api (aggregated by [hellomicroservice](https://github.com/bihero-io/hello-microservice) and [worldmicroservice](https://github.com/bihero-io/worldmicroservice)):
```bash
mvn clean package
java -jar target/helloworld-microservice-fat.jar
# using another terminal window
curl http://localhost:8080/ # Hello World
```

## How to configure connections to [hello](https://github.com/bihero-io/hello-microservice) and [world](https://github.com/bihero-io/worldmicroservice) microservices
Edit src/conf/config.json
```json
{
  "type": "file",
  "format": "json",
  "scanPeriod": 5000,
  "config": {
    "path": "/home/slava/JavaProjects/hello-world-to-cloud/helloworldmicroservice/src/conf/config.json"
  },
  "serverPort": 8083,
  "serverHost": "0.0.0.0",
  "hello-service-host": "localhost",
  "hello-service-port": 8081,
  "world-service-host": "localhost",
  "world-service-port": 8082
}
```
Run using config.json
```bash
java -jar target/world-microservice-fat.jar -conf src/conf/config.json
```