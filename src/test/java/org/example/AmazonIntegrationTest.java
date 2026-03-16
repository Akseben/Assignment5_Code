package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.example.Amazon.Amazon;
import org.example.Amazon.Database;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCartAdaptor;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AmazonIntegrationTest {
    
    private static Database db;
    private ShoppingCartAdaptor cart;
    private Amazon amazon;

    @BeforeAll
    static void initializeDB(){
        db = new Database();
    }

    @AfterAll
    static void closeDB(){
        db.close();
    }

    @BeforeEach
    void resetDB(){
        db.resetDatabase();
        cart = new ShoppingCartAdaptor(db);
        amazon = new Amazon(cart, List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));
    }




    @Test
    @DisplayName("specification-based")
    void testEmptyCartTotalZero(){
        assertEquals(0.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void TestOneNonElectronicItem(){
        amazon.addToCart(new Item(ItemType.OTHER, "Chalk Bag", 1, 30));
        // 30 base cost plus $5 delivery charge
        assertEquals(35.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void TestOneElectronicItem(){
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Speakers", 1, 80));
        // 80 base cost plus $5 delivery charge plus $7.5 electronic charge
        assertEquals(92.5, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void TestTwoElectronicItem(){
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Speakers", 1, 80));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Amp", 1, 30));
        // 80 + 30 base cost plus $5 delivery charge plus $7.5 electronic charge
        assertEquals(122.5, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void TestMixedItemTypes(){
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Speakers", 1, 80));
        amazon.addToCart(new Item(ItemType.OTHER, "Office Chair", 1, 150));
        // 80 + 150 base cost plus $5 delivery charge plus $7.5 electronic charge
        assertEquals(242.5, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void testFiveItems(){
        for (int i = 0; i < 5; i++){
            amazon.addToCart(new Item(ItemType.OTHER, "Notebook", 1, 4));
        }
        // 4 x 5 = 20 base cost plus $12.5 delivery charge
        assertEquals(32.5, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void testElevenItems(){
        for (int i = 0; i < 11; i++){
            amazon.addToCart(new Item(ItemType.OTHER, "Detergent", 1, 20));
        }
        // 20 x 11 = 220 base cost plus $20 delivery charge
        assertEquals(240.0, amazon.calculate());
    }




    @Test
    @DisplayName("structural-based")
    void testCartPersistency(){
        Item item = new Item(ItemType.ELECTRONIC, "Xbox Controller", 3, 60);
        Item item2 = new Item(ItemType.OTHER, "Hair brush", 1, 6);
        amazon.addToCart(item);
        amazon.addToCart(item2);

        List<Item> items = cart.getItems();
        assertEquals(2, items.size());
        assertEquals("Hair brush", items.get(1).getName());
        assertEquals(ItemType.ELECTRONIC, items.get(0).getType());
        assertEquals(3, items.get(0).getQuantity());
        assertEquals(6.0, items.get(1).getPricePerUnit());
    }

    @Test
    @DisplayName("structural-based")
    void testDeliveryBoundaryThreeVersusFour(){
        //add 3 items
        for (int i = 0; i < 3; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "TestA", 1, 0));
        }
        // price should be 5(just delivery prices)
        assertEquals(5, amazon.calculate());

        //add one more item
        amazon.addToCart(new Item(ItemType.OTHER, "TestB", 1, 0));
        assertEquals(12.5, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void testDeliveryBoundaryTenVersusEleven(){
        //add 3 items
        for (int i = 0; i < 10; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "TestA", 1, 0));
        }
        // price should be 5(just delivery prices)
        assertEquals(12.5, amazon.calculate());

        //add one more item
        amazon.addToCart(new Item(ItemType.OTHER, "TestB", 1, 0));
        assertEquals(20, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void testLargeQuantity(){
        amazon.addToCart(new Item(ItemType.OTHER, "Eraser", 1000, .2));
        assertEquals(205, amazon.calculate());
    }

}
