package com.sipios.refactoring.controller;

import com.sipios.refactoring.UnitTest;
import com.sipios.refactoring.api.model.Body;
import com.sipios.refactoring.api.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;

import static com.sipios.refactoring.constant.TypesConstant.CustomerType.*;
import static com.sipios.refactoring.constant.TypesConstant.ItemType.*;
import static org.mockito.Mockito.doReturn;

class ShoppingControllerTests extends UnitTest {

    @InjectMocks
    private ShoppingController controller;

    @Mock
    private Clock clock;

    private Clock fixedClock;

    AutoCloseable openMocks;

    @BeforeEach
    public void initMocks() {
        openMocks = MockitoAnnotations.openMocks(clock);
    }

    @AfterEach
    public void tearDown() throws Exception {
        openMocks.close();
    }

    void initClockNow() {
        LocalDate fixedLocalDate = LocalDate.now();
        returnFixedDateWhenClockGetInstant(fixedLocalDate);
    }

    @Test
    void should_return_0_if_item_empty() {
        initClockNow();
        String price = Assertions.assertDoesNotThrow(
            () -> controller.getPrice(new Body(new Item[] {}, "STANDARD_CUSTOMER"))
        );
        Assertions.assertEquals("0.0", price);
    }

    @Test
    void should_throw_if_standard_and_price_greater_than_200() {
        initClockNow();
        Item item = new Item();
        item.setNb(10);
        item.setType(TSHIRT);
        Body body = new Body();
        body.setType(STANDARD_CUSTOMER);
        body.setItems(new Item[]{item});
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> controller.getPrice(body));
        Assertions.assertEquals("Price (300.0) is too high for standard customer", exception.getReason());
    }

    @Test
    void should_throw_if_premium_and_price_greater_than_800() {
        initClockNow();
        Item item = new Item();
        item.setNb(30);
        item.setType(TSHIRT);
        Body body = new Body();
        body.setType(PREMIUM_CUSTOMER);
        body.setItems(new Item[]{item});
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> controller.getPrice(body));
        Assertions.assertEquals("Price (810.0) is too high for premium customer", exception.getReason());
    }

    @Test
    void should_throw_if_platinum_and_price_greater_than_2000() {
        initClockNow();
        Item item = new Item();
        item.setNb(180);
        item.setType(TSHIRT);
        Body body = new Body();
        body.setType(PLATINUM_CUSTOMER);
        body.setItems(new Item[]{item});
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> controller.getPrice(body));
        Assertions.assertEquals("Price (2700.0) is too high for platinum customer", exception.getReason());
    }

    @Test
    void should_throw_if_bad_customer() {
        initClockNow();
        Item item = new Item();
        item.setNb(180);
        item.setType(TSHIRT);
        Body body = new Body();
        body.setType("123");
        body.setItems(new Item[]{item});
        Assertions.assertThrows(ResponseStatusException.class, () -> controller.getPrice(body));
    }

    @Test
    void should_return_30_if_tshirt() {
        initClockNow();
        Assertions.assertEquals("30.0", controller.getPrice(new Body(new Item[] {new Item(TSHIRT,1)}, STANDARD_CUSTOMER)));
    }

    @Test
    void should_return_50_if_dress_exclu_sales() {
        LocalDate fixedLocalDate = LocalDate.of(2018, Month.JANUARY, 30);
        returnFixedDateWhenClockGetInstant(fixedLocalDate);

        Assertions.assertEquals("50.0", controller.getPrice(new Body(new Item[] {new Item(DRESS,1)}, STANDARD_CUSTOMER)));
    }

    @Test
    void should_return_100_if_jacket_exclu_sales() {
        LocalDate fixedLocalDate = LocalDate.of(2018, Month.JANUARY, 30);

        returnFixedDateWhenClockGetInstant(fixedLocalDate);

        Assertions.assertEquals("100.0", controller.getPrice(new Body(new Item[] {new Item(JACKET,1)}, STANDARD_CUSTOMER)));
    }

    @Test
    void should_discount_10_percent_if_premium() {
        initClockNow();
        Assertions.assertEquals("90.0", controller.getPrice(new Body(new Item[] {new Item(JACKET,1)}, PREMIUM_CUSTOMER)));
    }

    @Test
    void should_discount_50_percent_if_platinum() {
        initClockNow();
        Assertions.assertEquals("50.0", controller.getPrice(new Body(new Item[] {new Item(JACKET,1)}, PLATINUM_CUSTOMER)));
    }

    @Test
    void should_return_40_if_dress_on_winter_sales() {
        LocalDate fixedLocalDate = LocalDate.of(2018, Month.JANUARY, 10);

        returnFixedDateWhenClockGetInstant(fixedLocalDate);

        Assertions.assertEquals("40.0", controller.getPrice(new Body(new Item[] {new Item(DRESS,1)}, STANDARD_CUSTOMER)));
    }

    @Test
    void should_return_40_if_dress_on_summer_sales() {
        LocalDate fixedLocalDate = LocalDate.of(2018, Month.JUNE, 10);

        returnFixedDateWhenClockGetInstant(fixedLocalDate);

        Assertions.assertEquals("40.0", controller.getPrice(new Body(new Item[] {new Item(DRESS,1)}, STANDARD_CUSTOMER)));
    }

    @Test
    void should_return_90_if_jacket_on_winter_sales() {
        LocalDate fixedLocalDate = LocalDate.of(2018, Month.JANUARY, 10);

        returnFixedDateWhenClockGetInstant(fixedLocalDate);

        Assertions.assertEquals("90.0", controller.getPrice(new Body(new Item[] {new Item(JACKET,1)}, STANDARD_CUSTOMER)));
    }

    @Test
    void should_return_90_if_jacket_on_summer_sales() {
        LocalDate fixedLocalDate = LocalDate.of(2018, Month.JUNE, 10);

        returnFixedDateWhenClockGetInstant(fixedLocalDate);

        Assertions.assertEquals("90.0", controller.getPrice(new Body(new Item[] {new Item(JACKET,1)}, STANDARD_CUSTOMER)));
    }

    private void returnFixedDateWhenClockGetInstant(LocalDate fixedLocalDate) {
        fixedClock = Clock.fixed(fixedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
    }
}
