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

        List<Predicate> filters = applyFilters(request, builder, root);

        query.where(filters.toArray(new Predicate[filters.size()]));

        if (Metrics.CTR.equals(metrics)) {
            Expression<Long> sum1 = builder.sum(root.get("clicks"));
            Expression<Long> sum2 = builder.sum(root.get("impressions"));
            Expression<Double> ctr =  builder.quot(sum1, sum2).as(Double.class);

            query.select(builder.construct(ResultItem.class, root.get("datasource"), root.get("campaign"), ctr));
        } else {
            query.select(builder.construct(ResultItem.class, extractSelectParamsFromGroupsBy(aggregations, request.getGroupBy(), metrics, root, builder)));
        }

        if (request.getGroupBy() != null) {
            extractGroupsBy(cleanse(request.getGroupBy()), query, root);
        }

        return em.createQuery(query).getResultList();
    }

    @Override
    public Number calculateTotalAggregationNumbers(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Number> query = builder.createQuery(Number.class);
        Root<Marketing> root = query.from(Marketing.class);

        List<Predicate> filters = applyFilters(request, builder, root);

        query.select(calculateAggregator(aggregations, builder, root, metrics.name().toLowerCase()));

        query.where(filters.toArray(new Predicate[filters.size()]));
        return em.createQuery(query).getSingleResult();
    }

    private List<Predicate> applyFilters(MarketingQueryRequest request, CriteriaBuilder builder, Root<Marketing> root) {
        List<Predicate> filters = new ArrayList<>();

        if (request.getDatasource() != null) filters.add(builder.equal(root.get("datasource"), request.getDatasource()));
        if (request.getCampaign() != null) filters.add(builder.equal(root.get("campaign"), request.getCampaign()));

        if (request.getDate() != null) {
            filters.add(builder.between(root.get("daily"), request.getDate(), request.getDate()));
        } else if (request.getDateTo() != null && request.getDateTo() != null) {
            filters.add(builder.between(root.get("daily"), request.getDateFrom(), request.getDateTo()));
        }

        return filters;
    }
    
    private Expression calculateAggregator(Aggregations aggregations, CriteriaBuilder builder, Root<Marketing> root, String field) {
       if (Aggregations.MAX.equals(aggregations)) return builder.max(root.get(field));
       if (Aggregations.MIN.equals(aggregations)) return builder.min(root.get(field));
       if (Aggregations.AVG.equals(aggregations)) return builder.avg(root.get(field));

       return builder.sum(root.get(field));
    }

    private void extractGroupsBy(String groupBy, CriteriaQuery<ResultItem> query, Root<Marketing> root) {

        if (groupBy != null) {
            if (groupBy.contains(",")) {
                query.groupBy(stream(groupBy.split(",")).map(group -> root.get(group.toLowerCase()))
                        .collect(Collectors.toList()));
            } else {
                query.groupBy(root.get(groupBy.toLowerCase()));
            }
        }
    }

    private Selection[] extractSelectParamsFromGroupsBy(Aggregations aggregations, String groupBy, Metrics metrics, Root<Marketing> root, CriteriaBuilder builder) {
        List<Selection> results = new ArrayList<>();
        results.add(calculateAggregator(aggregations, builder, root, metrics.name().toLowerCase()));
        if (groupBy != null) {
            if (groupBy.contains(",")) {
                stream(groupBy.split(",")).sorted().forEach(group -> results.add(root.get(group.toLowerCase())));
            } else {
                results.add(root.get(groupBy.toLowerCase()));
            }
        }
        return results.toArray(new Selection[results.size()]);
    }
}
