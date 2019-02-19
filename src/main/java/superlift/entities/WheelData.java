package superlift.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains info from Tire&Wheel Tab. Consists of item sku and list of dataPairs (name of column and its value
 * for single table row. Its needed before first parse, as we don't know what exact column names we will find.
 */

@Entity
@Table(name = "wheelinfo")
public class WheelData {

    @Id
    @Column(name = "INFO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int infoID;

    @Column(name = "ITEM_SKU")
    private String itemSku;

    @Column(name = "TIRE")
    private String tire;

    @Column(name = "WHEEL")
    private String wheel;

    @Column(name = "BACKSPACING")
    private String backspacing;

    @Column(name = "OFFSET")
    private String offset;

    @Column(name = "BACKSPRING")
    private String backspring;

    @Column(name = "BACKSPACING_INCH")
    private String backspacingInch;

    @Column(name = "OFFSET_MM")
    private String offsetMM;

    @Override
    public String toString() {
        return "tire='" + tire + '\'' +
                ", wheel='" + wheel + '\'' +
                ", backspacing='" + backspacing + '\'' +
                ", offset='" + offset + '\'' +
                ", backspring='" + backspring + '\'' +
                ", backspacingInch='" + backspacingInch + '\'' +
                ", offsetMM='" + offsetMM + '\'';
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


    public String getTire() {
        return tire;
    }

    public void setTire(String tire) {
        this.tire = tire;
    }

    public String getWheel() {
        return wheel;
    }

    public void setWheel(String wheel) {
        this.wheel = wheel;
    }

    public String getBackspacing() {
        return backspacing;
    }

    public void setBackspacing(String backspacing) {
        this.backspacing = backspacing;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getBackspring() {
        return backspring;
    }

    public void setBackspring(String backspring) {
        this.backspring = backspring;
    }

    public String getBackspacingInch() {
        return backspacingInch;
    }

    public void setBackspacingInch(String backspacingInch) {
        this.backspacingInch = backspacingInch;
    }

    public String getOffsetMM() {
        return offsetMM;
    }

    public void setOffsetMM(String offsetMM) {
        this.offsetMM = offsetMM;
    }
}
