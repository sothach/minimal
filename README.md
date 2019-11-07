# Minimal
Efficiently process a sequence of integers to determine the minimal values
## Design
This is not a production-grade solution, but demonstrates the use of streaming to process
a large data set without a corresponding - and deleterious - increase is memory footprint
## API

#### Calculate minimum value

Create an Account for the authenticated User if an Account for that User does
not already exist. Each User can only have one Account.

**URL** : `/api/minimal`

**Method** : `POST`

**Auth required** : No

**Permissions required** : None

**Data constraints**

Provide comma-seperated list of integer values in the domain of `java.lang.Long`

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

**Content** : `no data provided`

**Content example**

## Run Service
Start the application locally:
```bash
% sbt run
```
Send data to the api:
```bash
%curl  http://localhost:9000/api/minimal -H "Content-type: multipart/form-data" -d @test/resources/bigfile.dat 
```
This will respond with the minimum value from the data-set:
```bash
-999968087
```

## Testing
The provided unit tests verify that integer values in the domain
-1000000000 thru 1000000000 are correctly processed, and invalid values
filtered out of the processing.  A data-set of 9999 randomll generated values
are used, see `test/resources/bigfile.dat`:
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
