package superlift;

import superlift.entities.SuperLiftItem;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    private List<SuperLiftItem> deletedItems = new ArrayList<>();
    private List<SuperLiftItem> addedItems = new ArrayList<>();


    public List<SuperLiftItem> getDeletedItems() {
        return deletedItems;
    }
    public List<SuperLiftItem> getAddedItems() {
        return addedItems;
    }
}
