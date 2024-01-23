package com.restful.booker.crudsuite;

import com.restful.booker.restfulinfo.BookingSteps;
import com.restful.booker.testbase.TestBase;
import com.restful.booker.utils.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.HashMap;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasKey;

@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookingCRUDTestWithSteps extends TestBase {

    public static String username = "admin";
    public static String password = "password123";
    public static String firstname = "Neil" + TestUtils.getRandomValue();
    public static String lastname = "Cooper" + TestUtils.getRandomValue();
    public static Integer totalprice = 810;
    public static Boolean depositpaid = true;
    public static String additionalneeds = "Bed and Breakfast";
    public static int bookingID;
    public static String token;

    @Steps
    BookingSteps steps;

    @Title("Auth token creation for a user")
    @Test
    public void test001() {
        ValidatableResponse response = steps.authUser(username, password);

        response.log().all().statusCode(200);

        HashMap<Object, Object> tokenMap= response.log().all().extract().path("");

        Assert.assertThat(tokenMap,hasKey("token"));
        String jsonString = response.extract().asString();
        token = JsonPath.from(jsonString).get("token");

        System.out.println(token);
    }

    @Title("Create the booking for a user")
    @Test
    public void test002() {

        HashMap<Object, Object> bookingsDatesData = new HashMap<>();
        bookingsDatesData.put("checkin", "2024-03-25");
        bookingsDatesData.put("checkout", "2024-04-11");

        ValidatableResponse response = steps.createBooking(firstname, lastname,totalprice,
                depositpaid,bookingsDatesData,additionalneeds);

        response.log().all().statusCode(200);
        bookingID= response.log().all().extract().path("bookingid");

        HashMap<Object,Object>bookingMap= response.log().all().extract().path("");
        Assert.assertThat(bookingMap,anything(firstname));
        System.out.println(token);
    }

    @Title("Verify booking is created")
    @Test
    public void test003() {

        ValidatableResponse response = steps.getBookingInfoByID(bookingID);
        response.log().all().statusCode(200);

    }

    @Title("Update a booking")
    @Test
    public void test004() {

        HashMap<Object, Object> bookingsDatesData = new HashMap<>();
        bookingsDatesData.put("checkin", "2024-03-27");
        bookingsDatesData.put("checkout", "2024-04-18");

        ValidatableResponse response = steps.updateBooking(bookingID,firstname, lastname,
                totalprice,depositpaid,bookingsDatesData,additionalneeds);

        response.log().all().statusCode(200);

        HashMap<Object,Object>bookingMap= response.log().all().extract().path("");
        Assert.assertThat(bookingMap,anything(firstname));
        System.out.println(token);
    }


    @Title("Partially update the booking")
    @Test
    public void test005() {
        HashMap<Object, Object> bookingsDatesData = new HashMap<>();
        bookingsDatesData.put("checkin", "2024-07-22");
        bookingsDatesData.put("checkout", "2025-08-28");

        ValidatableResponse response = steps.partialUpdateBooking(bookingID, firstname, lastname, bookingsDatesData);

        response.log().all().statusCode(200);

        HashMap<Object,Object>bookingMap= response.log().all().extract().path("");
        Assert.assertThat(bookingMap,anything(firstname));
        System.out.println(token);
    }


    @Title("Verify updated booking with bookingId")
    @Test
    public void test006() {

        ValidatableResponse response = steps.getUpdatedBookingInfoByID(bookingID);
        response.log().all().statusCode(200);

    }

    @Title("Deleted with BookingId")
    @Test
    public void test007() {

        ValidatableResponse response = steps.deleteBooking(bookingID);
        response.log().all().statusCode(201);

        ValidatableResponse response1 = steps.getBookingByID(bookingID);
        response1.log().all().statusCode(404);

    }
}