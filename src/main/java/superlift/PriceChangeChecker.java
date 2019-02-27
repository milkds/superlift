package superlift;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import superlift.entities.SuperLiftItem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class PriceChangeChecker {
    private static final Logger logger = LogManager.getLogger(PriceChangeChecker.class.getName());

    private String itemLink;

    public PriceChangeChecker(String itemLink) {
        this.itemLink = itemLink;
    }

    public String getItemLink() {
        return itemLink;
    }

    //Always Map with single k/v pair
    public Map<SuperLiftItem, BigDecimal> checkPrice(){
        logger.info("checking price for " + itemLink);
        SuperLiftItem item = SuperliftDAO.getItemByUrl(itemLink);
        String status = item.getStatus();
        if (status.equals("NOT AVAILABLE")||status.equals("DELETED")){
            return new HashMap<>();
        }
        BigDecimal oldPrice = item.getPrice();
        if (oldPrice==null) {
            return new HashMap<>();
        }

        BigDecimal newPrice = new JsoupParser().getNewPrice(itemLink);
        if (newPrice.intValue()!=0){
            if (newPrice.compareTo(oldPrice)!=0){
                SuperliftDAO.updatePriceForItem(item.getItemID(), newPrice);
                item.setPrice(newPrice);
                return Map.of(item, oldPrice);
            }
        }

        return new HashMap<>();
    }


}
