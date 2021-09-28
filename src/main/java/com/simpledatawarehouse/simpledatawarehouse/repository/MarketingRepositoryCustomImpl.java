package com.simpledatawarehouse.simpledatawarehouse.repository;

import com.simpledatawarehouse.simpledatawarehouse.controller.request.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.model.Marketing;
import com.simpledatawarehouse.simpledatawarehouse.model.ResultItem;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.simpledatawarehouse.simpledatawarehouse.util.StringUtils.COMMA;
import static com.simpledatawarehouse.simpledatawarehouse.util.StringUtils.cleanse;
import static java.util.Arrays.stream;

@Repository
public class MarketingRepositoryCustomImpl implements MarketingRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ResultItem> getMetricQueryResults(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ResultItem> query = builder.createQuery(ResultItem.class);
        Root<Marketing> root = query.from(Marketing.class);

        List<String> groupByValues= extractGroupBy(request.getGroupBy());

        query.where(applyFilters(request, builder, root));

        if (Metrics.CTR.equals(metrics)) {
            Expression<Long> sum1 = builder.sum(root.get("clicks"));
            Expression<Long> sum2 = builder.sum(root.get("impressions"));
            Expression<Double> ctr = builder.quot(sum1, sum2).as(Double.class);

            List<Expression> params = groupByValues.stream().sorted().map(root::get).collect(Collectors.toList());
            params.add(ctr);

            query.multiselect(params.toArray(Selection[]::new));
        } else {
            query.multiselect(extractSelectParamsFromGroupsBy(aggregations, groupByValues, metrics, root, builder));
        }

        if (request.getGroupBy() != null) {
            query.groupBy(extractGroupsBy(groupByValues, root));
            query.orderBy(extractGroupsForOrdering(groupByValues, root, builder));
        }

        return em.createQuery(query).getResultList();
    }

    @Override
    public Number calculateTotalAggregationNumbers(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Number> query = builder.createQuery(Number.class);
        Root<Marketing> root = query.from(Marketing.class);

        query.select(calculateAggregator(aggregations, builder, root, metrics.name().toLowerCase()));

        query.where(applyFilters(request, builder, root));
        return em.createQuery(query).getSingleResult();
    }

    private static List<String> extractGroupBy(String groupBy) {
        List<String> results = new ArrayList<>();

        if (groupBy.contains(COMMA)) {
            results = stream(cleanse(groupBy).split(COMMA)).collect(Collectors.toList());
        } else {
            results.add(cleanse(groupBy));
        }
        return results;
    }

    private static Predicate[] applyFilters(MarketingQueryRequest request, CriteriaBuilder builder, Root<Marketing> root) {
        List<Predicate> filters = new ArrayList<>();

        if (request.getDatasource() != null) filters.add(builder.equal(root.get("datasource"), request.getDatasource()));
        if (request.getCampaign() != null) filters.add(builder.equal(root.get("campaign"), request.getCampaign()));

        if (request.getDate() != null) {
            filters.add(builder.between(root.get("daily"), request.getDate(), request.getDate()));
        } else if (request.getDateFrom() != null && request.getDateTo() != null) {
            filters.add(builder.between(root.get("daily"), request.getDateFrom(), request.getDateTo()));
        } else if (request.getDateFrom() != null) {
            filters.add(builder.greaterThanOrEqualTo(root.get("daily"), request.getDateFrom()));
        } else if (request.getDateTo() != null) {
            filters.add(builder.lessThanOrEqualTo(root.get("daily"), request.getDateTo()));
        }

        return filters.toArray(Predicate[]::new);
    }
    
    private static Selection<? extends Number> calculateAggregator(Aggregations aggregations, CriteriaBuilder builder, Root<Marketing> root, String field) {
       if (Aggregations.MAX.equals(aggregations)) return builder.max(root.get(field));
       if (Aggregations.MIN.equals(aggregations)) return builder.min(root.get(field));
       if (Aggregations.AVG.equals(aggregations)) return builder.avg(root.get(field));

       return builder.sum(root.get(field));
    }

    private static Expression[] extractGroupsBy(List<String> groupByValues, Root<Marketing> root) {
        return groupByValues.stream().map(root::get).toArray(Expression[]::new);
    }

    private static List<Order> extractGroupsForOrdering(List<String> groupByValues, Root<Marketing> root, CriteriaBuilder builder) {
        return groupByValues.stream().map(groupByValue -> builder.asc(root.get(groupByValue))).collect(Collectors.toList());
    }

    private static Selection[] extractSelectParamsFromGroupsBy(Aggregations aggregations, List<String> groupByValues, Metrics metrics, Root<Marketing> root, CriteriaBuilder builder) {

        List<Selection> results = new ArrayList<>();
        results.add(calculateAggregator(aggregations, builder, root, metrics.name().toLowerCase()));
        groupByValues.stream().sorted().forEach(groupBy -> results.add(root.get(groupBy)));

        return results.toArray(Selection[]::new);
    }
}
