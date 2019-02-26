package superlift;

import superlift.entities.SuperLiftItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Statistics {

    private long totalItemsQuantityBeforeCheck;
    private long totalItemsQuantityAfterCheck;

    private Instant start;
    private Instant finish;

    private List<SuperLiftItem> deletedItems = new ArrayList<>();
    private List<SuperLiftItem> addedItems = new ArrayList<>();
    private Map<SuperLiftItem, BigDecimal> changedPricesMap = new HashMap<>();

    private Map<String, Long> categoryStartMap;
    private Map<String, Long> categoryFinishMap;

    private Map<String, List<SuperLiftItem>> addedItemsMap;
    private Map<String, List<SuperLiftItem>> deletedItemsMap;

    private StringBuilder statisticsKeeper = new StringBuilder();


    public Statistics() {
        init();
    }

    public File getReportFile() {
        File file = null;
        try
        {
            String fName = formatTime(finish);
            fName = fName.replaceAll(":", "-");
            fName = fName.substring(0, fName.length()-3);
            fName = fName+"_Superlift_parseReport.txt";
            fName = "C:/Dropbox/Superlift/"+ fName;
            file = new File(fName);
            //write data on temporary file
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(statisticsKeeper.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void init() {
        start = Instant.now();
        totalItemsQuantityBeforeCheck = StatisticsDAO.getTotalItems();
        categoryStartMap = getCategoryMap();
    }



    public void prepareReport() {
        fixateResults();
        showStatistics();
        System.out.println(statisticsKeeper.toString());
    }

    private void showStatistics() {
        printChangesByCategories();
        printChangesByItems();
        printPriceChanges();
        printTotals();
        printTime();
    }

    private void printTime() {
        statisticsKeeper.append("Parse started at: ");
        statisticsKeeper.append(formatTime(start));
        statisticsKeeper.append(System.lineSeparator());
        statisticsKeeper.append("Parse finished at: ");
        statisticsKeeper.append(formatTime(finish));
        statisticsKeeper.append(System.lineSeparator());
    }

    public static String formatTime(Instant instant) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM )
                        .withLocale( Locale.UK )
                        .withZone( ZoneId.systemDefault() );

        return formatter.format(instant);
    }

    private void printTotals() {
        statisticsKeeper.append("Total Items before check: ");
        statisticsKeeper.append(totalItemsQuantityBeforeCheck);
        statisticsKeeper.append(System.lineSeparator());
        statisticsKeeper.append("Total Items after check: ");
        statisticsKeeper.append(totalItemsQuantityAfterCheck);
        statisticsKeeper.append(System.lineSeparator());
        statisticsKeeper.append("Total Elapsed time in minutes: ");
        statisticsKeeper.append(Duration.between(start, finish).toMinutes());
        statisticsKeeper.append(System.lineSeparator());
    }

    private void printPriceChanges() {
        if (changedPricesMap.size()>0){
            statisticsKeeper.append("Prices changed:");
            changedPricesMap.forEach((k, v)->{
                statisticsKeeper.append(System.lineSeparator());
                statisticsKeeper.append("------------------------------");
                statisticsKeeper.append(System.lineSeparator());
                appendItem(k);
                BigDecimal newPrice = k.getPrice();
                statisticsKeeper.append(System.lineSeparator());
                statisticsKeeper.append("Old price: ");
                statisticsKeeper.append(v);
                statisticsKeeper.append("$. New price: ");
                statisticsKeeper.append(newPrice);
                statisticsKeeper.append("$. Difference: ");
                statisticsKeeper.append(newPrice.subtract(v));
                statisticsKeeper.append("$.");
                statisticsKeeper.append(System.lineSeparator());
                statisticsKeeper.append("------------------------------");
                statisticsKeeper.append(System.lineSeparator());
                statisticsKeeper.append(System.lineSeparator());
                    });

            appendVisualSep();
        }
    }

    private void printChangesByItems() {
        addedItemsMap.forEach((k,v)->{
            statisticsKeeper.append("Added ");
            appendCategory(k);
            v.forEach(item -> {
                statisticsKeeper.append("------------------------------");
                statisticsKeeper.append(System.lineSeparator());
                appendItem(item);
                statisticsKeeper.append(System.lineSeparator());
                statisticsKeeper.append("------------------------------");
                statisticsKeeper.append(System.lineSeparator());
            });
            v.forEach(this::appendItem);
            appendVisualSep();
            if (deletedItemsMap.containsKey(k)){
                statisticsKeeper.append("Deleted ");
                appendCategory(k);
                deletedItemsMap.get(k).forEach(this::appendItem);
                appendVisualSep();
            }
        });
        deletedItemsMap.forEach((k,v)->{
            if (!addedItemsMap.containsKey(k)){
                statisticsKeeper.append("Deleted ");
                appendCategory(k);
                v.forEach(this::appendItem);
                appendVisualSep();
            }
        });
    }

    private void appendVisualSep() {
        statisticsKeeper.append(System.lineSeparator());
        statisticsKeeper.append(System.lineSeparator());
        statisticsKeeper.append("------------------------------");
        statisticsKeeper.append(System.lineSeparator());
    }

    private void appendItem(SuperLiftItem item) {
        statisticsKeeper.append("SKU: ");
        statisticsKeeper.append(item.getPartNo());
        statisticsKeeper.append(System.lineSeparator());
        statisticsKeeper.append(item.getTitle());
        statisticsKeeper.append(System.lineSeparator());
        statisticsKeeper.append(item.getItemUrl());
        statisticsKeeper.append(System.lineSeparator());
    }

    private void appendCategory(String categoryName) {
        statisticsKeeper.append(categoryName);
        statisticsKeeper.append(":");
        statisticsKeeper.append(System.lineSeparator());
    }

    private void printChangesByCategories() {
        categoryFinishMap.forEach((k, v)->{
            statisticsKeeper.append("Category ");
            statisticsKeeper.append(k);
            statisticsKeeper.append(" : Items before check: ");
            if (categoryStartMap.containsKey(k)){
                statisticsKeeper.append(categoryStartMap.get(k));
            }
            else {
                statisticsKeeper.append("0");
            }
            statisticsKeeper.append(". Added: ");
            int added = 0;
            if (addedItemsMap.containsKey(k)){
                added = addedItemsMap.get(k).size();
            }
            statisticsKeeper.append(added);
            statisticsKeeper.append(". Deleted: ");
            int deleted = 0;
            if (deletedItemsMap.containsKey(k)){
                deleted = deletedItemsMap.get(k).size();
            }
            statisticsKeeper.append(deleted);
            statisticsKeeper.append(System.lineSeparator());
        });

        statisticsKeeper.append(System.lineSeparator());
    }

    private void fixateResults() {
        finish = Instant.now();
        sortAddedItems();
        sortDeletedItems();
        totalItemsQuantityAfterCheck = StatisticsDAO.getTotalItems();
        categoryFinishMap = getCategoryMap();
    }

    private Map<String,Long> getCategoryMap() {
        Map<String, Long> result = new HashMap<>();
        List<String> categoryNames = StatisticsDAO.getCategoryNames();
        categoryNames.forEach(catName->{
            Long itemQty = StatisticsDAO.getItemsQuantityByCategory(catName);
            result.put(catName, itemQty);
        });

        return result;
    }

    private void sortDeletedItems() {
        deletedItemsMap = sortItems(deletedItems);
    }

    private void sortAddedItems() {
        addedItemsMap = sortItems(addedItems);
    }

    /***
     * @param items - List of unsorted items
     * @return map, where Key is name of Category and Value is list of items from this category
     */
    private Map<String, List<SuperLiftItem>> sortItems(List<SuperLiftItem> items){
        Map<String, List<SuperLiftItem>> result = new HashMap<>();
        items.forEach(item->{
            String categoryName = item.getCategory();
            List<SuperLiftItem> itemsFromCategory = new ArrayList<>();
            if (result.containsKey(categoryName)){
                itemsFromCategory = result.get(categoryName);
                itemsFromCategory.add(item);
            }
            else {
                itemsFromCategory.add(item);
                result.put(categoryName, itemsFromCategory);
            }
        });
        return result;
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
    public StringBuilder getStatisticsKeeper() {
        return statisticsKeeper;
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

    public void setTotalItemsQuantityBeforeCheck(long totalItemsQuantityBeforeCheck) {
        this.totalItemsQuantityBeforeCheck = totalItemsQuantityBeforeCheck;
    }
    public void setTotalItemsQuantityAfterCheck(long totalItemsQuantityAfterCheck) {
        this.totalItemsQuantityAfterCheck = totalItemsQuantityAfterCheck;
    }
    public void setStart(Instant start) {
        this.start = start;
    }
    public void setFinish(Instant finish) {
        this.finish = finish;
    }
    public void setDeletedItems(List<SuperLiftItem> deletedItems) {
        this.deletedItems = deletedItems;
    }
    public void setAddedItems(List<SuperLiftItem> addedItems) {
        this.addedItems = addedItems;
    }
    public void setChangedPricesMap(Map<SuperLiftItem, BigDecimal> changedPricesMap) {
        this.changedPricesMap = changedPricesMap;
    }
    public Map<String, Long> getCategoryStartMap() {
        return categoryStartMap;
    }
    public void setCategoryStartMap(Map<String, Long> categoryStartMap) {
        this.categoryStartMap = categoryStartMap;
    }
    public Map<String, Long> getCategoryFinishMap() {
        return categoryFinishMap;
    }
    public void setCategoryFinishMap(Map<String, Long> categoryFinishMap) {
        this.categoryFinishMap = categoryFinishMap;
    }
    public Map<String, List<SuperLiftItem>> getAddedItemsMap() {
        return addedItemsMap;
    }
    public void setAddedItemsMap(Map<String, List<SuperLiftItem>> addedItemsMap) {
        this.addedItemsMap = addedItemsMap;
    }
    public Map<String, List<SuperLiftItem>> getDeletedItemsMap() {
        return deletedItemsMap;
    }
    public void setDeletedItemsMap(Map<String, List<SuperLiftItem>> deletedItemsMap) {
        this.deletedItemsMap = deletedItemsMap;
    }
    public void setStatisticsKeeper(StringBuilder statisticsKeeper) {
        this.statisticsKeeper = statisticsKeeper;
    }
}
