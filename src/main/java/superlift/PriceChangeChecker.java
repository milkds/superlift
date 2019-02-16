package superlift;

import superlift.entities.SuperLiftItem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class PriceChangeChecker {

    private String itemLink;

    public PriceChangeChecker(String itemLink) {
        this.itemLink = itemLink;
    }

    public String getItemLink() {
        return itemLink;
    }

    //Always Map with single k/v pair
    public Map<SuperLiftItem, BigDecimal> checkPrice(){
        SuperLiftItem item = SuperliftDAO.getItemByUrl(itemLink);
        BigDecimal oldPrice = item.getPrice();
        if (oldPrice==null) {
            return new HashMap<>();
        }

        BigDecimal newPrice = new JsoupParser().getNewPrice(itemLink);
        if (newPrice.intValue()!=0){
            if (newPrice.compareTo(oldPrice)!=0){
                SuperliftDAO.updatePriceForItem(item.getItemID(), newPrice);
                return Map.of(item, oldPrice);
            }
        }

        return new HashMap<>();
    }


}
