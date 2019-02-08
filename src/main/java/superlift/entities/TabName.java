package superlift.entities;

import javax.persistence.*;

@Entity
@Table(name = "tabnames")
public class TabName {

    @Id
    @Column(name = "TAB_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tabID;

    @Column(name = "ITEM_SKU")
    private String itemSKU;

    @Column(name = "TAB_NAME")
    private String tabName;

    @Override
    public String toString() {
        return "TabName{" +
                "itemSKU='" + itemSKU + '\'' +
                ", tabName='" + tabName + '\'' +
                '}';
    }

    public int getTabID() {
        return tabID;
    }

    public void setTabID(int tabID) {
        this.tabID = tabID;
    }

    public String getItemSKU() {
        return itemSKU;
    }

    public void setItemSKU(String itemSKU) {
        this.itemSKU = itemSKU;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }


}
