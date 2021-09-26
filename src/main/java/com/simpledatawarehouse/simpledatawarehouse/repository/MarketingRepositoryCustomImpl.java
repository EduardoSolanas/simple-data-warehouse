package com.simpledatawarehouse.simpledatawarehouse.repository;

import com.simpledatawarehouse.simpledatawarehouse.controller.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;
import com.simpledatawarehouse.simpledatawarehouse.model.Marketing;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
    public Long sumClicks(MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Marketing> root = query.from(Marketing.class);

        List<Predicate> filters = new ArrayList<>();

        query.select(builder.sum(root.get("clicks")));

        if (request.getDatasource() != null) filters.add(builder.equal(root.get("datasource"), request.getDatasource()));
        if (request.getCampaign() != null) filters.add(builder.equal(root.get("campaign"), request.getCampaign()));

        if (request.getDate() != null) {
            filters.add(builder.between(root.get("daily"), request.getDate(), request.getDate()));
        } else if (request.getDateTo() != null && request.getDateTo() != null) {
            filters.add(builder.between(root.get("daily"), request.getDateFrom(), request.getDateTo()));
        }

        query.where(filters.toArray(new Predicate[filters.size()]));
        return em.createQuery(query).getSingleResult();
    }

    @Override
    public List<ImpressionsOverTime> getImpressions(MarketingQueryRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ImpressionsOverTime> query = builder.createQuery(ImpressionsOverTime.class);
        Root<Marketing> root = query.from(Marketing.class);

        List<Predicate> filters = new ArrayList<>();

        query.select(builder.construct(ImpressionsOverTime.class,  builder.sum(root.get("impressions")), root.get("daily")));

        if (request.getDatasource() != null) filters.add(builder.equal(root.get("datasource"), request.getDatasource()));
        if (request.getCampaign() != null) filters.add(builder.equal(root.get("campaign"), request.getCampaign()));

        if (request.getDate() != null) {
            filters.add(builder.between(root.get("daily"), request.getDate(), request.getDate()));
        } else if (request.getDateTo() != null && request.getDateTo() != null) {
            filters.add(builder.between(root.get("daily"), request.getDateFrom(), request.getDateTo()));
        }

        query.groupBy(root.get("daily"));
        query.where(filters.toArray(new Predicate[filters.size()]));

        return em.createQuery(query).getResultList();
    }
}
