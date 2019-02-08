package superlift.entities;

import javax.persistence.*;

@Entity
@Table(name = "titles")
public class Title {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int titleID;

    @Column (name = "TITLE")
    private String title;

    @Column (name = "URL")
    private String itemUrl;

    @Column (name = "CATEGORY")
    private String category;


    @Override
    public String toString() {
        return "Title{" +
                "title='" + title + '\'' +
                ", itemUrl='" + itemUrl + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    public int getTitleID() {
        return titleID;
    }

    public void setTitleID(int titleID) {
        this.titleID = titleID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
