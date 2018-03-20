# User Analytic

Web service to keep user analytic ( timestamp,userid, {click | impression})

Adding /Post: around 6000 requests per second

Consuming / Get: few requests per second

**Techno:** Http Akka, Slick, Mysql, Flyway, Gatling.

## How to run it:

1/ follow the MySql steps

2/ execute **"sbt run"**


### Mysql
```
>brew install mysql
>brew start mysql
>mysqladmin -u root password 'yourpassword'
>mysql -u root -p
```

note: if mysql is not in your path, you can use "brew info mysql" to see the path.



```
CREATE USER 'fcuseranalytic'@'LOCALHOST' IDENTIFIED BY 'change0me';
CREATE DATABASE analyticdb;
GRANT ALL ON analyticdb.* TO 'fcuseranalytic'@'localhost';
```

#### check the content:

```
use analyticdb
select * from UserAnalytic
```

-----

## ScalaStyle:

```
sbt scalastyleGenerateConfig (only the first time)
sbt scalastyle
```



## Load testing:

### Gatling :

```
>sbt GatlingTesting/gatling:test**
```

Only for the post as we have only a few request per second in get.
I would recommend to run it a few times (the first time, the result are
kind of bad, depending on the OS,memory allocation,..)
