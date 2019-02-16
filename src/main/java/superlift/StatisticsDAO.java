package superlift;

import org.hibernate.Session;
import superlift.HibernateUtil;
import superlift.entities.SuperLiftItem;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
}
