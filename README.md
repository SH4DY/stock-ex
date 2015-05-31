# stock-ex
Stock exchange simulation based on a space based and an alternative middleware solution.

# Running Apps and prerequisites:

## 1. Install RabbitMQ
https://www.rabbitmq.com/download.html

<!--## 2. Run RabbitMQ Broker
```sh 
$ rabbitmq-server
```-->

## 2. Build projects
```sh 
$ mvn clean install
```
<!--## 4. Run space server:
```sh
$ java -cp java -cp [project-root]/Server/mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar:Domain/target/domain-1.0-SNAPSHOT.jar org.mozartspaces.core.Server
```
## 5. Run applications (via java -jar)
```sh
java -jar [project-root]/[application-dir]/target/[application]-1.0-SNAPSHOT.jar --spring.profiles.active=[amqp,space] --[additional_arg]=[value]
```
* **Market**: [application-dir] = Market, [application] = market

e.g. 
```sh
$ java -jar Market/target/market-1.0-SNAPSHOT.jar --spring.profiles.active=space
```
*  **MarketAgent**: [application-dir] = MarketAgent, [application] = marketagent

e.g. 
```sh
$ java -jar MarketAgent/target/marketagent-1.0-SNAPSHOT.jar --spring.profiles.active=space
```

*  **Investor**: [application-dir] = Investor, [application] = investor, arg_id(integer), arg_budget(double)

e.g. 
```sh 
$ java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=123 --budget=10000.0
```
*  **Broker**: [application-dir] = Broker, [application] = broker, arg_id(integer)

e.g. 
```sh 
$ java -jar Broker/target/broker-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=1
```

*  **Company**: [application-dir] = Company, [application] = broker, arg_id(string), arg_numShares(int), arg_initPrice(double)

e.g. 

```sh 
$ java -jar Company/target/company-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=GOO --numShares=100
```
-->
## 3. Run applications (via run.sh)

* **Space or Amqp environment**: ./run.sh env [space,amqp] [?spacePort]

e.g. 
```sh 
$ ./run.sh env space 9876
$ ./run.sh env space 9877
$ ./run.sh env ampq
```

* **Market**: ./run.sh market [space,amqp] [host][":"[port],"/"[exchange]]

e.g. 
```sh 
$ ./run.sh market space localhost:9876
$ ./run.sh market space localhost:9877
$ ./run.sh market amqp localhost/stockExchange1
$ ./run.sh market amqp localhost/stockExchange2

```
*  **MarketAgent**: ./run.sh marketagent [space,amqp] [host][":"[port],"/"[exchange]],...

e.g. 
```sh 
$ ./run.sh marketagent space localhost:9876|localhost:9877
$ ./run.sh marketagent space localhost:9876
$ ./run.sh marketagent amqp  localhost/stockExchange1#localhost/stockExchange2
$ ./run.sh marketagent amqp  localhost/stockExchange1


```
*  **Investor/Fondmanager**: ./run.sh investor ["space","amqp"] [host][":"[port],"/"[exchange]]#[budget]#[numShares],...

e.g. 
```sh 
$ ./run.shinvestor space fondmanager1 FOND_MANAGER localhost:9876#10000#2000,localhost:9877#2000
$ ./run.shinvestor space investor1 INVESTOR localhost:9876#10000,localhost:9877#2000
$ ./run.shinvestor space fondmanager1 FOND_MANAGER localhost/stockExchange1#10000#2000,localhost/stockExchange2#2000
$ ./run.shinvestor space investor1 INVESTOR localhost/stockExchange1#10000,localhost/stockExchange2#2000

```
* **Broker**: ./run.sh broker ["space","amqp"] [id(int)] [host][":"[port],"/"[exchange]]

e.g. 
```sh 
$ ./run.sh broker space 12 localhost:9876
$ ./run.sh broker amqp 12 localhost/stockExchange1
```
* **Company**: ./run.sh company ["space","amqp"] [id(string)] [host][":"[port],"/"[exchange]]#[numShares]#[initPrice]

e.g. 
```sh 
$ ./run.sh company space GOO localhost:9876#100#10
$ ./run.sh company space AMA localhost:9877#100#10
$ ./run.sh company amqp GOO localhost/stockExchange1#100#10
$ ./run.sh company amqp AMA localhost/stockExchange2#100#10
```

