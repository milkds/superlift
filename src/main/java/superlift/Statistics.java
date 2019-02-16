package superlift;

import superlift.entities.SuperLiftItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    private long totalItemsQuantityBeforeCheck;
    private long totalItemsQuantityAfterCheck;

    private Instant start;
    private Instant finish;


    private List<SuperLiftItem> deletedItems = new ArrayList<>();
    private List<SuperLiftItem> addedItems = new ArrayList<>();
    private Map<SuperLiftItem, BigDecimal> changedPricesMap = new HashMap<>();

    private StringBuilder reportKeeper = new StringBuilder();


    public Statistics() {
        init();
    }

    private void init() {
        start = Instant.now();
        totalItemsQuantityBeforeCheck = StatisticsDAO.getTotalItems();
    }

    public List<SuperLiftItem> getDeletedItems() {
        return deletedItems;
    }
    public List<SuperLiftItem> getAddedItems() {
        return addedItems;
    }
    public Map<SuperLiftItem, BigDecimal> getChangedPricesMap() {
        return changedPricesMap;
    }
    public StringBuilder getReportKeeper() {
        return reportKeeper;
    }

    public void prepareReport() {
        fixateResults();
        showStatistics();
    }

    private void showStatistics() {
    }

    private void fixateResults() {

    }


    public long getTotalItemsQuantityBeforeCheck() {
        return totalItemsQuantityBeforeCheck;
    }

    public long getTotalItemsQuantityAfterCheck() {
        return totalItemsQuantityAfterCheck;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getFinish() {
        return finish;
    }
}
