#!/bin/bash

if (($# >= 1 ))
then

case  $1  in
	env)
		if [ "$2" == "space" ]; then
		  java -cp java -cp Server/mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar:Domain/target/domain-1.0-SNAPSHOT.jar org.mozartspaces.core.Server $3
	    fi
		if [ "$2" == "amqp" ]; then
			rabbitmq-server
		fi
		;;
	market)	
		java -jar Market/target/market-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --market=$3
		;;
	marketagent)	
		java -jar MarketAgent/target/marketagent-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --market=$3
		;;
	investor)
	    if (($# >= 5 )); then
		  java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --id=$3 --type=$4 --market=$5
	    else
		  echo Additional arguments for markets needed!	
		fi
		;;
	broker)
	    if (($# >= 4 )); then
		  java -jar Broker/target/broker-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --id=$3 --market=$4
	    else
		  echo Additional argument for MARKET needed!	
		fi
		;;
	company)
	    if (($# >= 4 )); then
		  java -jar Company/target/company-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --id=$3 --market=$4
	    else
		  echo Additional arguments for MARKET needed!	
		fi
		;;
    marketdirectory)
        java -jar MarketDirectory/target/marketdirectory-1.0-SNAPSHOT.jar
        ;;
	
	*)
esac

else
	echo Too less arguments
fi
		