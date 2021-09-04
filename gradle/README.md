# coding-lagom

## Prerequisites
* Docker (version 1.11 or later is installed and running).
* Docker Compose is installed. Docker Compose is installed by default with Docker for Mac. Docker memory is allocated minimally at 6 GB. (When using Docker Desktop for Mac, the default Docker memory allocation is 2 GB. You can change the default allocation to 6 GB in Docker. Navigate to `Preferences > Resources > Advanced.`)
* Internet connectivity
* (Optional) curl.

## Set up a development/testings stand

If we don't want to (or cannot) use the sbt/maven plugins for lagom development we can 
use such approach (leas automatic, however more flexible):  
* set up a kafka cluster (if we use it at all)
* set up a cassandra db (if we use it, and it is our choose of persistence backend)
* run our lagom app in cluster (with debug option or not)

### Set up a kafka cluster
#### Download and Start Confluent Platform Using Docker

This in fact will install and run the full set of tools from Confluent Platform (Community version) (https://confluent.io)
and sometimes can be considered as `too much` however if you use those extra tools it can be very handy for you.

1. Download or copy the contents of the Confluent Community all-in-one Docker Compose file:
```shell
curl --silent --output docker-compose.yml \
https://raw.githubusercontent.com/confluentinc/cp-all-in-one/6.2.0-post/cp-all-in-one-community/docker-compose.yml
```

2. Start Confluent Platform:
```shell
docker-compose up -d
```

3. Create a topic `test`:
```shell
docker-compose exec broker kafka-topics \
  --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic test
```

4. Stop Confluent Containers and Clean Up:
```shell
docker-compose stop

# After stopping the Docker containers, run the following commands 
# to prune the Docker system. 
# Running these commands deletes containers, networks, 
# volumes, and images, freeing up disk space.
docker system prune -a --volumes --filter "label=io.confluent.docker"
```

### Set up a cassandra db
1. Pull the latest image:
```shell
docker pull cassandra:latest
```
2. Run image (it will start a cassandra instance available on 9042/tcp port)
```shell
docker run -p 9042:9042 --name cassandra cassandra
```
If the default port should be replaced use such command
```shell
docker run -p 9043:9042 --name cassandra cassandra
```
3. Create a file with your DML like this:
```csh
-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS store WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create a table
CREATE TABLE IF NOT EXISTS store.shopping_cart (
userid text PRIMARY KEY,
item_count int,
last_update_timestamp timestamp
);

-- Insert some data
INSERT INTO store.shopping_cart
(userid, item_count, last_update_timestamp)
VALUES ('9876', 2, toTimeStamp(now()));
INSERT INTO store.shopping_cart
(userid, item_count, last_update_timestamp)
VALUES ('1234', 5, toTimeStamp(now()));
```
4. Connect to CQLSH :
```shell
docker exec -it cassandra /opt/cassandra/bin/cqlsh
```
5. type the content of the file from the step 3 into this shell. 
6. Stop Cassandra and Clean Up:
```shell
docker kill cassandra
docker network rm cassandra
```


----------
application.mode=prod
java -Dhttp.port=9000 -Dakka.remote.artery.canonical.port=25520 -Dakka.management.http.port=8558 -Dpidfile.path="/dev/null" -jar service-a-impl-all.jar