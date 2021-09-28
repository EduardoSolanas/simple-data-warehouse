# Getting Started

I have decided to defined 2 endpoints, mostly for the different response types, 'totals' with just a number and the other endpoint that returns a list of items.

CTR has been considered a metric in order to simplify the number of endpoints.


### Reference Documentation
There are the following metrics:
* CTR (requires a groupBy params)
* Clicks
* Impressions

There are the following aggregations:
* SUM
* AVG
* MAX
* MIN

All endpoints can be filtered by :
* Daily ("MM/dd/yyyy"): Filters by a given date
* DateFrom ("MM/dd/yyyy"): Filters by date greater or equals than the given dateFrom  
* DateTo ("MM/dd/yyyy"): Filters by date lower or equals than the given dateTo
* DateFrom and DateTo: Filters by the given date range
* Datasource: Filters by datasource value
* Campaign: Filters by campaign value

The endpoints that support grouping allow the following fields to be grouped by:
* daily
* campaign
* datasource

There are 3 endpoints:
 * /{metrics}/total/{aggregations} Calculates the total for the given metric and the given aggregation. It does not support grouping
 * /{metrics}/{aggregations} Calculates the aggregation of the given metric. Supports grouping.
 * /{metrics} Calculates the sum of the given metric. Supports grouping. This is the endpoint to use when using CTR because adding an aggregation on top of the CTR is not supported.

## Application deployment
The application has been deployed in this environment:

http://simpledatawarehouse-env.eba-v3pxyp9n.eu-west-1.elasticbeanstalk.com/marketing/


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



## Possible improvements
* Add a way to combine CTR with some aggregations, like for example: return the AVG(CTR) for a month
* Add Pagination, so if there is a lot of data you can consume it in pages.
* Add better sorting. At the moment is asc by default and using the values from the groupBy param.
