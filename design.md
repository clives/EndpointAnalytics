# Design - UserAnalytic

## Requirement:

4000-6000 post request per Second
a few get request per Second


### DB:

#### Option:

**Nosql** - InflixDB, TimeScale.

**Sql** - mysql, postgress.

**Choice**: For the number of request by second, sql  database is enough,
here we have some result for postgress: https://blog.timescale.com/timescaledb-vs-6a696248104e
So any sql db could be use, I choosed Mysql for it's simplicity
(easy to install locally), but at long term I would recommend postgress, that would
give use the possibility to use TimeScale Db, if needed ( based on postgress, improve
storage,  time of response for time series data "spliting" the data
using the field "timestamp", src: https://www.timescale.com/


### Http server:

Http Akka based on this recommendation:
https://dzone.com/articles/akka-http-vs-other-rest-api-tools


### Project Structure:

multi project based on sbt. Separating as much as possible the server,
the service and the Db. The service is defined inside the db,we could create
another project latter. The main project is the server, choose to not
create a specific "project" for it, as we want to use play, scaltra,.. we
need to change the whole structure anyway (hard to have some flexibility on this part)


### Testing:

http service. db service. An integration test should be added.
And I would recommend a test based on gatling for load performance:
https://gatling.io/

