java -cp java -cp Server/mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar:Domain/target/domain-1.0-SNAPSHOT.jar org.mozartspaces.core.Server 9876 &
java -cp java -cp Server/mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar:Domain/target/domain-1.0-SNAPSHOT.jar org.mozartspaces.core.Server 9877 &

java -jar MarketDirectory/target/marketdirectory-1.0-SNAPSHOT.jar &

java -jar Market/target/market-1.0-SNAPSHOT.jar --spring.profiles.active=space --market=localhost:9876 &
java -jar Market/target/market-1.0-SNAPSHOT.jar --spring.profiles.active=space --market=localhost:9877 &

java -jar MarketAgent/target/marketagent-1.0-SNAPSHOT.jar --spring.profiles.active=space --market=localhost:9876 &
java -jar MarketAgent/target/marketagent-1.0-SNAPSHOT.jar --spring.profiles.active=space --market=localhost:9877 &

java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=fm1 --type=FOND_MANAGER --market=localhost:9876#10000#2000,localhost:9877#2000 &
java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=i1 --type=INVESTOR --market=localhost:9876#10000#2000,localhost:9877#2000 &

java -jar Broker/target/broker-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=12 --market=localhost:9876 &
java -jar Broker/target/broker-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=13 --market=localhost:9877 &

java -jar Company/target/company-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=GOOG --market=localhost:9876#100#10 &
java -jar Company/target/company-1.0-SNAPSHOT.jar --spring.profiles.active=space --id=APPL --market=localhost:9876#100#10 &
