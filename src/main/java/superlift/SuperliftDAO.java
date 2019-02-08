package superlift;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import superlift.entities.*;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SuperliftDAO {
    private static final Logger logger = LogManager.getLogger(SuperliftDAO.class.getName());


    public static List<String> getItemsBySubcategory(String subCatName) {
        //impl
        return new ArrayList<>();
    }

    public static SuperLiftItem markItemDeleted(String dbLink) {
        //impl

        return new SuperLiftItem();
    }

    public static void saveItem(SuperLiftItem item) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(item);
            List<Fitment> fits = item.getFits();
            if (fits!=null&&fits.size()>0){
                fits.forEach(fit->{
                    fit.getItems().add(item);
                    session.persist(fit);
                });
            }
            String sku = item.getPartNo();
            List<TabName> tabNames = item.getTabNames();
            if (tabNames!=null&&tabNames.size()>0){
                tabNames.forEach(tabName -> {
                    tabName.setItemSKU(sku);
                    session.persist(tabName);
                });
            }
            List<WheelData> wheelDatas = item.getWheelData();
            if (wheelDatas!=null&&wheelDatas.size()>0){
                wheelDatas.forEach(wheelData -> {
                    session.persist(wheelData);
                    List<WheelDataPair> dataPairs = wheelData.getInfoPairs();
                    dataPairs.forEach(dataPair->{
                        dataPair.setWheelData(wheelData);
                        session.persist(dataPair);
                    });
                });
            }

            transaction.commit();
            session.close();
            logger.info("saved item: " + item);
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
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

    public static List<String> getUrlsToReparse() {
        Session session = HibernateUtil.getSession();
        List<String> urls = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<Title> root = crQ.from(Title.class);
        crQ.select(root.get("itemUrl"));
        Query q = session.createQuery(crQ);
        urls = q.getResultList();
        session.close();


        return urls;
    }

    public static void updateTitle(Title title) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            Title dbTitle = getTitleByUrl(session, title.getItemUrl());
            dbTitle.setTitle(title.getTitle());
            session.update(dbTitle);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    private static Title getTitleByUrl(Session session, String itemUrl) {
        Title title = null;
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Title> crQ = builder.createQuery(Title.class);
        Root<Title> root = crQ.from(Title.class);
        crQ.where(builder.equal(root.get("itemUrl"),itemUrl));
        Query q = session.createQuery(crQ);
        title = (Title)q.getSingleResult();

        return title;
    }

    public static List<Title> getAllTitles() {
        Session session = HibernateUtil.getSession();
        List<Title> titles = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Title> crQ = builder.createQuery(Title.class);
        Root<Title> root = crQ.from(Title.class);
        Query q = session.createQuery(crQ);
        titles = q.getResultList();
        session.close();


        return titles;
    }

    public static void updateTitleCategory(Title title) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            Title dbTitle = session.get(Title.class, title.getTitleID());
            dbTitle.setCategory(title.getCategory());
            session.update(dbTitle);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public static List<String> getAllItemUrls() {
        Session session = HibernateUtil.getSession();
        List<String> urls = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<SuperLiftItem> root = crQ.from(SuperLiftItem.class);
        crQ.where(builder.notEqual(root.get("status"), "DELETED")).select(root.get("itemUrl"));
        Query q = session.createQuery(crQ);
        urls = q.getResultList();
        session.close();

        return urls;
    }

    public static SuperLiftItem deleteItemByUrl(String url) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        SuperLiftItem item = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            item = getItemByUrl(url, session);
            item.setStatus("DELETED");
            session.update(item);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }

        return item;
    }

    private static SuperLiftItem getItemByUrl(String url, Session session) {
        SuperLiftItem item = null;
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SuperLiftItem> crQ = builder.createQuery(SuperLiftItem.class);
        Root<SuperLiftItem> root = crQ.from(SuperLiftItem.class);
        crQ.where(builder.equal(root.get("itemUrl"), url));
        Query q = session.createQuery(crQ);
        item = (SuperLiftItem)q.getSingleResult();

        return item;
    }
}
