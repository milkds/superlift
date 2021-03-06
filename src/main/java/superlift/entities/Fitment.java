package superlift.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "fits")
public class Fitment {

    @Id
    @Column(name = "FIT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fitID;

    @Column (name = "CAR")
    private String car;

    @Column (name = "YEAR_START")
    private Integer yearStart;

    @Column (name = "YEAR_FINISH")
    private Integer yearFinish;

    @ManyToMany(mappedBy = "fits")
    private List<SuperLiftItem> items = new ArrayList<>();

    @Override
    public String toString() {
        return "car='" + car + '\'' +
                ", yearStart=" + yearStart +
                ", yearFinish=" + yearFinish;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public Integer getYearStart() {
        return yearStart;
    }

    public void setYearStart(Integer yearStart) {
        this.yearStart = yearStart;
    }

    public Integer getYearFinish() {
        return yearFinish;
    }

    public void setYearFinish(Integer yearFinish) {
        this.yearFinish = yearFinish;
    }

    public int getFitID() {
        return fitID;
    }

    public void setFitID(int fitID) {
        this.fitID = fitID;
    }

    public List<SuperLiftItem> getItems() {
        return items;
    }

    public void setItems(List<SuperLiftItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fitment)) return false;
        Fitment fitment = (Fitment) o;
        return fitID == fitment.fitID &&
                Objects.equals(car, fitment.car) &&
                Objects.equals(yearStart, fitment.yearStart) &&
                Objects.equals(yearFinish, fitment.yearFinish);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fitID, car, yearStart, yearFinish);
    }
}
