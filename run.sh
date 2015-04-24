#!/bin/bash

if (($# >= 2 ))  
then

case  $1  in
	env)
		if [ "$2" == "space" ]; then
		  java -cp java -cp Server/mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar:Domain/target/domain-1.0-SNAPSHOT.jar org.mozartspaces.core.Server
	    fi
		if [ "$2" == "amqp" ]; then
			rabbitmq-server
		fi
		;;
	market)	
		java -jar Market/target/market-1.0-SNAPSHOT.jar --spring.profiles.active=$2
		;;
	marketagent)	
		java -jar MarketAgent/target/marketagent-1.0-SNAPSHOT.jar --spring.profiles.active=$2
		;;
	investor)
	    if (($# >= 4 )); then
		  java -jar Investor/target/investor-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --id=$3 --budget=$4
	    else
		  echo Additional arguments for ID and BUDGET needed!	
		fi
		;;
	broker)
	    if (($# >= 3 )); then
		  java -jar Broker/target/broker-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --id=$3 
	    else
		  echo Additional argument for ID needed!	
		fi
		;;
	company)
	    if (($# >= 5 )); then
		  java -jar Company/target/company-1.0-SNAPSHOT.jar --spring.profiles.active=$2 --id=$3 --numShares=$4 --initPrice=$5
	    else
		  echo Additional arguments for ID, NUM_SHARES, INIT_PRICE needed!	
		fi
		;;
	
	*)
esac

else
	echo Too less arguments
fi
		