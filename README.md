# Getting Started

### Reference Documentation
There are the following metrics:
* CTR (requires always a groupBy param)
* Clicks
* Impressions

There are the following aggregations:
* SUM
* AVG
* MAX
* MIN

All endpoints can be filtered by :
* Daily: Filters by a given date
* DateFrom and DateTo: Filters by a range of dates
* Datasource: Filters by datasource value
* Campaign: Filters by campaign value

There are 3 endpoints:
 * /{metrics}/total/{aggregations} Calculates the total for the given metric and the given aggregation. It does not support grouping
 * /{metrics}/{aggregations} Calculates the aggregation of the given metric. Supports grouping.
 * /{metrics} Calculates the sum of the given metric. Supports grouping.

## Examples
* Will return total clicks for a given Datasource for a given Date range
```
curl --location --request POST 'http://simpledatawarehouse-env.eba-v3pxyp9n.eu-west-1.elasticbeanstalk.com/marketing/clicks/total/sum' \
--header 'Content-Type: application/json' \
--data-raw '{"datasource": "Google Ads","dateFrom":"09/12/2019","dateTo": "11/12/2019"}'
```
* Will return click-Through Rate (CTR) per Datasource and Campaign
```
curl --location --request POST 'http://simpledatawarehouse-env.eba-v3pxyp9n.eu-west-1.elasticbeanstalk.com/marketing/ctr' \
--header 'Content-Type: application/json' \
--data-raw '{"groupBy": "datasource,campaign"}'
```

* Will return impressions over time (daily)
```
curl --location --request POST 'http://simpledatawarehouse-env.eba-v3pxyp9n.eu-west-1.elasticbeanstalk.com/marketing/impressions/sum' \
--header 'Content-Type: application/json' \
--data-raw '{"groupBy": "daily"}'
```


## Application deployment
The application has been deployed in this environment:

http://simpledatawarehouse-env.eba-v3pxyp9n.eu-west-1.elasticbeanstalk.com/marketing/

## Possible improvements
* Add a way to combine CTR with some aggregations, like for example: return the AVG(CTR) for a month
* Add Pagination
* Add sorting