package superlift;

import org.hibernate.Session;
import org.hibernate.Transaction;
import superlift.entities.SuperLiftItem;
import superlift.entities.Title;

import java.util.ArrayList;
import java.util.List;

public class SuperliftDAO {
    public static List<String> getItemsBySubcategory(String subCatName) {
        //impl
        return new ArrayList<>();
    }

    public static SuperLiftItem markItemDeleted(String dbLink) {
        //impl

        return new SuperLiftItem();
    }

    public static void saveItem(SuperLiftItem newItem) {
        //impl
    }

    public static void saveTitle(Title title) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(title);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
