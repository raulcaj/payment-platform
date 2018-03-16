# payment-platform
A sample payment platform with transaction and account modules

## Running this sample

First go into eureka-ds directory and execute 'mvn spring-boot:run', it will start a discovery server.  
Then you can start account-module and transaction-module in any other with 'mvn spring-boot:run' within them respectives directories.

## What can you do?

### **/v1/accounts/{id}**
	**PATH**  
		you can send modification to a account
	**GET**  
		you can get a account by id

### **/v1/accounts/limits**
	**GET**  
		you can retrieve all accounts

### **/v1/transactions**
	**POST**  
		you can send new credit/withdrawal transactions
	**GET**  
		you can get all transactions

### **/v1/payments**
	**POST**  
		you can send new payment transactions

### Test
