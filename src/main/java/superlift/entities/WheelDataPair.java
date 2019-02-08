package superlift.entities;

import javax.persistence.*;

@Entity
@Table(name = "wheeldatapair")
public class WheelDataPair {

    @Id
    @Column(name = "PAIR_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pairID;

    @Column(name = "PAIR_NAME")
    private String name;

    @Column(name = "PAIR_VALUE")
    private String value;

    @ManyToOne
    @JoinColumn(name = "INFO_ID")
    private WheelData wheelData;

    @Override
    public String toString() {
        return "WheelDataPair{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public int getPairID() {
        return pairID;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPairID(int pairID) {
        this.pairID = pairID;
    }

    public WheelData getWheelData() {
        return wheelData;
    }

    public void setWheelData(WheelData wheelData) {
        this.wheelData = wheelData;
    }
}
