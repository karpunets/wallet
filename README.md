
## How to run the client and the server

### Build docker images
```bash
./gradlew dockerBuildImage
```

### wallet-server
```bash
# up
docker-compose -f wallet-server/docker-compose.yml up -d
# down
docker-compose -f wallet-server/docker-compose.yml down
```

### wallet-client
```bash
# up
docker-compose -f wallet-client/docker-compose.yml up
# down
docker-compose -f wallet-client/docker-compose.yml down

```

## Properties with default values

### wallet-server
```properties
# needs for tests
WALLET_SERVER_ACTIVE=true
WALLET_SERVER_PORT=9090
```

### wallet-client
```properties
WALLET_SERVER_HOST=localhost
WALLET_SERVER_PORT=9090
# Number of concurrent users emulated.
WALLET_CLI_USERS=1
# Number of concurrent requests a user will make
WALLET_CLI_CONCURRENT_THREADS_PER_USER=1
# Number of rounds each thread is executing.
WALLET_CLI_ROUNDS_PER_THREAD=1
```

## Some explanation

1. Was added user creation method on the server because it's more simple to writing tests and using in *wallet-client*.
2. Each deposit or withdraw calls creates _AccountTransaction_ because it's more maintained. 
And we can easily add features like showing all transaction or making some statistics.
3. _WalletService_ inject itself because it uses inner transaction methods.
4. Used _UUID_ instead of _Long_ for ids because there is no security and client could just increment own id and get access to someone else's wallet.
5. For adding new _Round_ you can just create a new bean and implement one method. 
6. If is need an explanation of something just ask


## Estimate

```
MacBook Pro (15-inch, 2018)
Processor 2.2 GHz Intel Core i7
Memmory 16 GB 2400 MHz DDR4
```

> _RoundMetric_ class collected the estimate and gave approximate values and would be log before destroying the application


```yaml
wallet:
  cli:
    users: Table.Users
    concurrent-threads-per-user: Table.Threads
    rounds-per-thread: table.Rounds
```


| Users | Threads | Rounds | total second | per second |
| ----- | ------- | ------ | ----------   | ---------- |
| 100   | 16      | 10     | 12           | 531        |
| 500   | 16      | 3      | 18           | 529        |
| 10    | 16      | 100    | 14           | 455        |
| 100   | 100     | 10     | 13           | 488        |


Because server save each account transaction and don't save sum account amount, 
the server doesn't need to make pessimistic locking on account entities

To improve performance server also can cache _User_ entities 


