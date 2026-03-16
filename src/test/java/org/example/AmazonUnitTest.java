package org.example;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
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
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

public class AmazonUnitTest {
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
    @DisplayName("structural-test")
    void testNumberOfItems(){
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Speaker", 2, 80));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "DAC", 1, 20));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Sound bar", 1, 200));

        assertEquals(3, cart.numberOfItems());
    }

    @Test
    @DisplayName("structural-test")
    void testSameConnectionOnMultipleObjects(){
        Database testDB = new Database();
        assertSame(db.getConnection(), testDB.getConnection());
    }

    @Test
    @DisplayName("structural-test")
    void testNullCloseConnection(){
        Database testDB = new Database();
        // closes connection once
        testDB.close();
        // ensures that closing it again doesnt throw errors
        assertDoesNotThrow(() -> testDB.close());
    }
}
