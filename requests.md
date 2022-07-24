* `curl -s http://localhost:8080/topjava/rest/meals | jq` - get all meals
* `curl -s http://localhost:8080/topjava/rest/meals/100009 | jq` - get meal by id
* `curl -s -X POST -d '{"dateTime": "2020-01-31T12:00", "description": "test", "calories": 1000}' -H 'Content-Type:application/json' http://localhost:8080/topjava/rest/meals | jq` - create meal
* `curl -s -X PUT -d '{"dateTime": "2020-01-31T15:00", "description": "update", "calories": 100}' -H 'Content-Type: application/json' http://localhost:8080/topjava/rest/meals/100005` - update meal by id
* `curl -s -X DELETE http://localhost:8080/topjava/rest/meals/100005` - delete meal by id
* `curl -s "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&startTime=10:00:00&endDate=2020-01-30&endTime=13:00:00" | jq` - get meals filtered by params
