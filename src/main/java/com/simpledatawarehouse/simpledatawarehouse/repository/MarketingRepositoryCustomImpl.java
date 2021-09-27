package com.simpledatawarehouse.simpledatawarehouse.repository;

import com.simpledatawarehouse.simpledatawarehouse.controller.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;
import com.simpledatawarehouse.simpledatawarehouse.model.Marketing;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MarketingRepositoryCustomImpl implements MarketingRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CTR> calculateCTR(MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<CTR> query = builder.createQuery(CTR.class);
        Root<Marketing> root = query.from(Marketing.class);

        Expression<Long> sum1 = builder.sum(root.get("clicks"));
        Expression<Long> sum2 = builder.sum(root.get("impressions"));

        query.select(builder.construct(CTR.class, root.get("datasource"), root.get("campaign"),  builder.quot(sum1, sum2).as(Double.class)));
        if (request.getDate() != null) {
            query.where(builder.between(root.get("daily"), request.getDate(), request.getDate()));
        } else if (request.getDateTo() != null && request.getDateTo() != null) {
            query.where(builder.between(root.get("daily"), request.getDateFrom(), request.getDateTo()));
        }

        query.groupBy(root.get("datasource"), root.get("campaign"));

        return em.createQuery(query).getResultList();
    }

    @Override
    public Number queryApplyingAggregator(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Number> query = builder.createQuery(Number.class);
        Root<Marketing> root = query.from(Marketing.class);

        List<Predicate> filters = new ArrayList<>();

        query.select(calculateAggregator(aggregations, builder, root, metrics.name().toLowerCase()));

        applyFilters(request, builder, root, filters);

        query.where(filters.toArray(new Predicate[filters.size()]));
        return em.createQuery(query).getSingleResult();
    }

    @Override
    public List<ImpressionsOverTime> getMetricQueryResults(Metrics metrics, MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ImpressionsOverTime> query = builder.createQuery(ImpressionsOverTime.class);
        Root<Marketing> root = query.from(Marketing.class);

        List<Predicate> filters = new ArrayList<>();

        query.select(builder.construct(ImpressionsOverTime.class,  builder.sum(root.get(metrics.name().toLowerCase())), root.get("daily")));

        applyFilters(request, builder, root, filters);

        if (request.getGroupBy() != null) {
            query.groupBy(root.get(request.getGroupBy().toString().toLowerCase()));
        }

        query.where(filters.toArray(new Predicate[filters.size()]));

        return em.createQuery(query).getResultList();
    }

    private void applyFilters(MarketingQueryRequest request, CriteriaBuilder builder, Root<Marketing> root, List<Predicate> filters) {
        if (request.getDatasource() != null) filters.add(builder.equal(root.get("datasource"), request.getDatasource()));
        if (request.getCampaign() != null) filters.add(builder.equal(root.get("campaign"), request.getCampaign()));

        if (request.getDate() != null) {
            filters.add(builder.between(root.get("daily"), request.getDate(), request.getDate()));
        } else if (request.getDateTo() != null && request.getDateTo() != null) {
            filters.add(builder.between(root.get("daily"), request.getDateFrom(), request.getDateTo()));
        }
    }
    
    private Expression calculateAggregator(Aggregations aggregations, CriteriaBuilder builder, Root<Marketing> root, String field) {
       if (aggregations.equals(Aggregations.MAX)) return builder.max(root.get(field));
       if (aggregations.equals(Aggregations.MIN)) return builder.min(root.get(field));
       if (aggregations.equals(Aggregations.AVG)) return builder.avg(root.get(field));


       return builder.sum(root.get(field));
    }
}
