Running Apps:

1. Build projects
mvn clean install

2. Run space server:
java -cp java -cp Server/mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar:Domain/target/domain-1.0-SNAPSHOT.jar org.mozartspaces.core.Server

3. Run applications
java -jar [project-root]/[application-dir]/target/[application]-1.0-SNAPSHOT.jar --spring.profiles.active=[amqp,space]

@Ramon:
- Das sind mal die voräufigen Buid Scripts
- Wenn du was im Domain Modul änderst, dann musst ein mvn clean install machen
- Wenn du ein Modul von IntelliJ startest must du in der RunConfiguration von jeder main Class die "Program Arguments setzten" _> --spring.profiles.active=[amqp,space]