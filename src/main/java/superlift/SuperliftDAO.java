package superlift;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import superlift.entities.*;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SuperliftDAO {
    private static final Logger logger = LogManager.getLogger(SuperliftDAO.class.getName());

    //ItemGroup section
    ////////////////////////////////////////
    public static List<String> getItemsBySubcategory(String subCatName) {
        Session session = HibernateUtil.getSession();
        List<String> urls = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<ItemGroup> root = crQ.from(ItemGroup.class);
        crQ.where(builder.equal(root.get("subCatName"), subCatName)).select(root.get("groupUrl"));
        Query q = session.createQuery(crQ);
        urls = q.getResultList();
        session.close();

        return urls;
    }

    public static ItemGroup markItemDeleted(String dbLink) {
        ItemGroup item = new ItemGroup();
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            item = getItemGroupByUrl(session, dbLink);
            item.setStatus("DELETED");
            transaction.commit();
            session.close();
            logger.info("marked itemGroup as deleted: " + item);
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }

        return item;
    }

    public static void saveItemGroup(ItemGroup item) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(item);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }

    }

    private static ItemGroup getItemGroupByUrl(Session session, String dbLink) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ItemGroup> crQ = builder.createQuery(ItemGroup.class);
        Root<ItemGroup> root = crQ.from(ItemGroup.class);
        crQ.where(builder.and(builder.equal(root.get("groupUrl"), dbLink),
                builder.equal(root.get("status"), "ACTIVE")));
        Query q = session.createQuery(crQ);

        return (ItemGroup) q.getSingleResult();
    }
    /////////////////////////////////////////////////////////////////


    public static void saveItem(SuperLiftItem item) {
        if (itemExists(item)){
            return;
        }
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
            List<WheelData> wheelDatas = item.getWheelData();
            if (wheelDatas!=null&&wheelDatas.size()>0){
                wheelDatas.forEach(session::persist);
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
    }

    private static boolean itemExists(SuperLiftItem item) {
        Session session = HibernateUtil.getSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SuperLiftItem> crQ = builder.createQuery(SuperLiftItem.class);
        Root<SuperLiftItem> root = crQ.from(SuperLiftItem.class);
        crQ.where(builder.equal(root.get("itemUrl"), item.getItemUrl()));
        Query q = session.createQuery(crQ);
        try (session) {
        SuperLiftItem testItem = (SuperLiftItem) q.getSingleResult();
        } catch (NoResultException e) {
            return false;
        }

        return true;
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

       try{
           item = (SuperLiftItem)q.getSingleResult();
       }
       catch (NoResultException e){
           logger.error("No item for url" + url);
           session.close();
           HibernateUtil.shutdown();
           System.exit(1);
       }

        return item;
    }

    public static SuperLiftItem getItemByUrl(String url){
        Session session = HibernateUtil.getSession();
        SuperLiftItem result = getItemByUrl(url,session);
        session.close();

        return result;
    }

    public static void updatePriceForItem(int itemID, BigDecimal newPrice) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        SuperLiftItem item = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            item = session.get(SuperLiftItem.class, itemID);
            item.setPrice(newPrice);
            session.update(item);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }

    }

    public static List<SuperLiftItem> getAllItems() {
        Session session = HibernateUtil.getSession();
        List<SuperLiftItem> items = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SuperLiftItem> crQ = builder.createQuery(SuperLiftItem.class);
        Root<SuperLiftItem> root = crQ.from(SuperLiftItem.class);
        crQ.where(builder.and(builder.notEqual(root.get("status"), "DELETED"), builder.notEqual(root.get("status"), "NOT AVAILABLE")));
        Query q = session.createQuery(crQ);
        items = q.getResultList();
        session.close();


        return items;

    }

    public static List<WheelData> getWheelDataForItem(Session session, SuperLiftItem item) {
        List<WheelData> wheelDatas = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WheelData> crQ = builder.createQuery(WheelData.class);
        Root<WheelData> root = crQ.from(WheelData.class);
        crQ.where(builder.equal(root.get("itemSku"), item.getPartNo()));
        Query q = session.createQuery(crQ);
        try{
            wheelDatas = q.getResultList();
        }
        catch (NoResultException e){
        }

        return wheelDatas;
    }

    public static List<Fitment> getFitmentsForItem(Session session, SuperLiftItem item) {
        SuperLiftItem newItem = session.find(SuperLiftItem.class, item.getItemID());
        List<Fitment> fits = newItem.getFits();
        item.setFits(fits);
        return fits;
    }


}
