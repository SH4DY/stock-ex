# stock-ex
Stock exchange simulation based on a space based and an alternative middleware solution.

# Running Apps and prerequisites:

## 1. Install RabbitMQ
https://www.rabbitmq.com/download.html

## 2. Run RabbitMQ Broker
```sh 
$ rabbitmq-server
```

## 3. Build projects
```sh 
$ mvn clean install
```
## 4. Run space server:
```sh
$ java -cp java -cp [project-root]/Server/mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar:Domain/target/domain-1.0-SNAPSHOT.jar org.mozartspaces.core.Server
```
## 5. Run applications (via java -jar)
```sh
java -jar [project-root]/[application-dir]/target/[application]-1.0-SNAPSHOT.jar --spring.profiles.active=[amqp,space] --[additional_arg]=[value]
```
* Market: [application-dir] = Market, [application] = market
e.g. 
```sh
$ java -jar Market/target/market-1.0-SNAPSHOT.jar --spring.profiles.active=space
```
* ##### MarketAgent: [application-dir] = MarketAgent, [application] = marketagent
e.g. 
```sh
$ java -jar MarketAgent/target/marketagent-1.0-SNAPSHOT.jar --spring.profiles.active=space
```

* ##### Investor: [application-dir] = Investor, [application] = investor, arg_id(integer), arg_budget(double)
e.g. 
```sh 
$ java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=123 --budget=10000.0
```
* ##### Broker: [application-dir] = Broker, [application] = broker, arg_id(integer)
e.g. 
```sh 
$ java -jar Broker/target/broker-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=1
```
* ##### Company: [application-dir] = Company, [application] = broker, arg_id(string), arg_numShares(int), arg_initPrice(double)
e.g. 
```sh 
$ java -jar Company/target/company-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=GOO --numShares=100 ```

## 5.1 Run applications (via run.sh)

* ##### Space or Amqp environment: ./run.sh env [space,amqp]
e.g. 
```sh 
$ ./run.sh env space
```
e.g. 
```sh 
$ ./run.sh env amqp
```
* ##### Market: ./run.sh market [space,amqp]
e.g. 
```sh 
$ ./run.sh market space
```
* ##### MarketAgent: ./run.sh marketagent [space,amqp]
e.g. 
```sh 
$ ./run.sh marketagent space
```
* ##### Investor: ./run.sh investor [space,amqp] [id(int)] [budget(double)]
e.g. 
```sh 
$ ./run.sh investor space 123 10000.0
```
* ##### Broker: ./run.sh broker [space,amqp] [id(int)]
e.g. 
```sh 
$ ./run.sh broker space 1
```
* ##### Company: ./run.sh company [space,amqp] [id(string)] [numShares(int)] [initPrice(double)]
e.g. 
```sh 
$ ./run.sh company space GOO 100 10.0
```

