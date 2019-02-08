package superlift.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains info from Tire&Wheel Tab. Consists of item sku and list of dataPairs (name of column and its value
 * for single table row. Its needed before first parse, as we don't know what exact column names we will find.
 */

@Entity
@Table(name = "wheeldata")
public class WheelData {

    @Id
    @Column(name = "INFO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int infoID;

    @Column(name = "ITEM_SKU")
    private String itemSku;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wheelData")
    private List<WheelDataPair> infoPairs = new ArrayList<>();

    @Override
    public String toString() {
        return "WheelData{" +
                "infoID=" + infoID +
                ", itemSku='" + itemSku + '\'' +
                ", infoPairs=" + infoPairs +
                '}';
    }

    public int getInfoID() {
        return infoID;
    }

    public void setInfoID(int infoID) {
        this.infoID = infoID;
    }

    public String getItemSku() {
        return itemSku;
    }

    public void setItemSku(String itemSku) {
        this.itemSku = itemSku;
    }

    public List<WheelDataPair> getInfoPairs() {
        return infoPairs;
    }

    public void setInfoPairs(List<WheelDataPair> infoPairs) {
        this.infoPairs = infoPairs;
    }


}
