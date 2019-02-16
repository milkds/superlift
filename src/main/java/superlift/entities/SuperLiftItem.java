package superlift.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "items")
public class SuperLiftItem {

    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemID;

    @Column (name = "TITLE")
    private String title;

    @Column (name = "WTF")
    private String wtf;

    @Column (name = "INCLUDE")
    private String include;

    @Column (name = "PART_NO")
    private String partNo;

    @Column (name = "PRICE")
    private BigDecimal price;

    @Column (name = "IMG_LINKS")
    private String imgLinks;

    @Column (name = "KEY_DETAILS")
    private String keyDetails;

    @Column (name = "NOTES")
    private String notes;

    @Column (name = "INSTALL_INFO")
    private String installInfo;

    @Column (name = "CATEGORY")
    private String category;

    @Column (name = "ITEM_URL")
    private String itemUrl;

    @Column (name = "STATUS")
    private String status;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "items_fits",
            joinColumns = { @JoinColumn(name = "ITEM_ID") },
            inverseJoinColumns = { @JoinColumn(name = "FIT_ID") }
    )
    private List<Fitment> fits = new ArrayList<>();

    @Transient
    private List<WheelData> wheelData = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuperLiftItem)) return false;
        SuperLiftItem item = (SuperLiftItem) o;
        return itemID == item.itemID &&
                Objects.equals(partNo, item.partNo) &&
                Objects.equals(itemUrl, item.itemUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, partNo, itemUrl);
    }

    @Override
    public String toString() {
        return "SuperLiftItem{" +
                "title='" + title + '\'' +
                ", partNo='" + partNo + '\'' +
                ", price=" + price +
                ", imgLinks='" + imgLinks + '\'' +
                ", category='" + category + '\'' +
                ", itemUrl='" + itemUrl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWtf() {
        return wtf;
    }

    public void setWtf(String wtf) {
        this.wtf = wtf;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImgLinks() {
        return imgLinks;
    }

    public void setImgLinks(String imgLinks) {
        this.imgLinks = imgLinks;
    }

    public String getKeyDetails() {
        return keyDetails;
    }

    public void setKeyDetails(String keyDetails) {
        this.keyDetails = keyDetails;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInstallInfo() {
        return installInfo;
    }

    public void setInstallInfo(String installInfo) {
        this.installInfo = installInfo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Fitment> getFits() {
        return fits;
    }

    public void setFits(List<Fitment> fits) {
        this.fits = fits;
    }

    public List<WheelData> getWheelData() {
        return wheelData;
    }

    public void setWheelData(List<WheelData> wheelData) {
        this.wheelData = wheelData;
    }
}
