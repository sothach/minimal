# Minimal
Efficiently process a sequence of integers to determine the minimal value
## Design
This is not a production-grade solution, but demonstrates the use of streaming to process
a large data set without a corresponding - and deleterious - increase is memory footprint
## Configuration
The maximum length of allowed frames (in this case, discerete integer values) while decoding, set in `conf/application.conf`:
```bash
input.frame.size=1000
```
## API

### Calculate minimum value

Given a data-set consisting of a set of comma-seperated Long integer values (positive and negative), determine and answer with the lowest value

**URL** : `/api/minimal`

**Method** : `POST`

**Auth required** : No

**Permissions required** : None

**Data constraints**

Provide comma-separated list of integer values in the domain of `java.lang.Long`

**Data example** 

```java
-994275329,
906862559,
-36626088,
743799515,
-201965995,
-21821772,
322782558,
-422943259,
-956974090
```

## Success Response

**Condition** : If data set processed ok

**Code** : `200 SUCCESS`

**Content example**

```json
-999968087
```

## Error Responses

**Condition** : If empty data-set provided

**Code** : `400 BAD REQUEST`

**Content example** : `no data provided`

## Run Service
Start the application locally:
```bash
% sbt run
```
Send data to the api:
```bash
% curl  http://localhost:9000/api/minimal -H "Content-type: multipart/form-data" -d @test/resources/bigfile.dat 
```
This will respond with the minimum value from the data-set:
```bash
-999968087
```
## Metrics
### Retrieve JVM metrics
API to retrieve JVM metrics values from running service (@codahale)

**URL** : `/metrics`

**Method** : `GET`

**Auth required** : No

**Permissions required** : None

**Data example** 

```json
{
  "version" : "4.0.0",
  "gauges" : {
    "jvm.attribute.name" : {
      "value" : "73662@PDQ17526984"
    },
    "jvm.attribute.uptime" : {
      "value" : 150893
    },
    "jvm.attribute.vendor" : {
      "value" : "Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 25.192-b12 (1.8)"
    }
  },
  "timers" : { }
}
```

## Testing
The provided unit tests verify that integer values in the domain
-1000000000 thru 1000000000 are correctly processed, and invalid values
filtered out of the processing.  A data-set of 9999 randomly-generated values
is used, see `test/resources/bigfile.dat`:
```bash
sort --numeric-sort bigfile.dat | head 
-999968087
-999800455
-999597000
-999008584
-998948663
-998653326
-998463590
-998305391
-998182735
-999968087
-998018946
```

## Author
* [Roy Phillips](mailto:phillips.roy@gmail.com)
