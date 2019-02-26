package superlift;

import org.hibernate.Session;
import superlift.HibernateUtil;
import superlift.entities.SuperLiftItem;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {

    public static long getTotalItems() {
        Session session = HibernateUtil.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> crQ = builder.createQuery(Long.class);
        Root<SuperLiftItem> root = crQ.from(SuperLiftItem.class);
        crQ.select(builder.count(root.get("itemID")));
        crQ.where(builder.equal(root.get("status"), "ACTIVE"));
        Query q = session.createQuery(crQ);
        Long result = (Long)q.getSingleResult();
        session.close();

        return result;
    }

    public static List<String> getCategoryNames() {
        Session session = HibernateUtil.getSession();
        List<String> categoryNames = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SuperLiftItem> crQ = builder.createQuery(SuperLiftItem.class);
        Root<SuperLiftItem> root = crQ.from(SuperLiftItem.class);
        crQ.distinct(true).select(root.get("category"));
        Query q = session.createQuery(crQ);
        categoryNames = q.getResultList();
        session.close();

        return categoryNames;
    }

    public static Long getItemsQuantityByCategory(String catName) {
        Session session = HibernateUtil.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> crQ = builder.createQuery(Long.class);
        Root<SuperLiftItem> root = crQ.from(SuperLiftItem.class);
        crQ.select(builder.count(root.get("itemID")));
        crQ.where(builder.and(builder.equal(root.get("category"), catName),
                builder.equal(root.get("status"), "ACTIVE")));
        Query q = session.createQuery(crQ);
        Long itemQty = (Long)q.getSingleResult();
        session.close();

        return itemQty;
    }
}
