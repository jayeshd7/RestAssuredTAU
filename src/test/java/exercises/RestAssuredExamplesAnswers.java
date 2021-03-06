package exercises;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import dataentities.Address;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.*;
import io.restassured.specification.*;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.*;

@RunWith(DataProviderRunner.class)
public class RestAssuredExamplesAnswers {




    public static RequestSpecification requestspec;

    @BeforeClass
    public static void createRequestSpecification(){

        requestSpec = new RequestSpecBuilder().
                setBaseUri("http://api.zippopotam.us").build();

    }

    @Test
    public void validateCountryForZipCode() {

        given().
        when().
            get("http://api.zippopotam.us/us/90210").           // Do a GET call to the specified resource
        then().
            assertThat().                                      // Assert that the value of the element 'country'
                contentType(ContentType.JSON);// in the response body equals 'United States'
    }


    @Test
    public void validateCountryContentType() {

        given().
                when().
                get("http://api.zippopotam.us/us/90210").           // Do a GET call to the specified resource
                then().
                assertThat().                                      // Assert that the value of the element 'country'
                contentType(ContentType.JSON);// in the response body equals 'United States'
    }

    @Test
    public void validateCountryBody() {

        given().
                log().all().
        when().
                get("http://api.zippopotam.us/us/90210").
        then().
                log().body();
    }


    @DataProvider
    public static Object[][] zipCodeAndPlaces(){
        return new Object[][]{
                {"us","90210","Beverly Hills"},
                {"us","12345","Schenectady"},
                {"ca","B2R","Waverley"}


        };

    }
    @Test
    @UseDataProvider("zipCodeAndPlaces")
    public void validatePlacesBody(String country,String zipCode,String placeName) {

        given().
                spec(requestSpec).
                pathParam("countryCode",country).pathParam("zipCode",zipCode).
        when().
                get("/{countryCode}/{zipCode}").
        then().
                assertThat().body("places[0].'place name'",equalTo(placeName));


    }


    @Test
    public void validateHashSizeBody() {

        given().
                log().all().
                when().
                get("http://api.zippopotam.us/us/90210").
                then().
                assertThat().body("places.'state'",hasItem("California"));


    }

    //Data Provider





    @Test
    public void checkResponseHeaders() {

        given().
                log().all().
        when().
            get("http://api.zippopotam.us/us/90210").
        then().
            assertThat().
            statusCode(200).
        and().
            contentType(ContentType.JSON);
    }

    @Test
    public void useQueryParameter() {

        given().
            queryParam("text", "testcase").
        when().
            get("http://md5.jsontest.com").
        then().
            assertThat().
            body("md5", equalTo("7489a25fc99976f06fecb807991c61cf"));
    }

    @Test
    public void usePathParameter() {

        given().
            pathParam("driver","max_verstappen").
        when().
            get("http://ergast.com/api/f1/drivers/{driver}.json").
        then().
            assertThat().
            body("MRData.DriverTable.Drivers.permanentNumber[0]", equalTo("33"));
    }



    @Test
    public void useBasicAuthentication() {

        given().
            auth().
            preemptive().
            basic("username", "password").
        when().
            get("https://my.secure/api").
        then().
            assertThat().
            statusCode(200);
    }

    @Test
    public void useOAuthAuthentication() {

        given().
            auth().
            oauth2("myAuthenticationToken").
        when().
            get("https://my.very.secure/api").
        then().
            assertThat().
            statusCode(200);
    }

    @Test
    public void checkResponseTimeForApiCall() {

        given().
        when().
            get("http://ergast.com/api/f1/circuits/monza.json").
        then().
            assertThat().
            time(lessThan(100L), TimeUnit.MILLISECONDS);
    }

    private static ResponseSpecification responseSpec;

    @BeforeClass
    public static void createResponseSpec() {

        responseSpec =
            new ResponseSpecBuilder().
                expectStatusCode(200).
                expectContentType(ContentType.JSON).
                build();
    }

    @Test
    public void useResponseSpec() {

        given().
        when().
            get("http://api.zippopotam.us/us/90210").
        then().
            spec(responseSpec).
        and().
            body("country", equalTo("United States"));
    }

    private static RequestSpecification requestSpec;

    @BeforeClass
    public static void createRequestSpec() {

        requestSpec =
            new RequestSpecBuilder().
                setBaseUri("http://ergast.com").
                setBasePath("/api/f1").
                build();
    }

    @Test
    public void useRequestSpec() {

        given().
            spec(requestSpec).
        when().
            get("/circuits/monza.json").
        then().
            assertThat().
            statusCode(200);
    }

    @Test
    public void serializeAddressToJson() {

        Address myAddress = new Address("My street", 1, 1234, "Amsterdam");

        given().
            body(myAddress).
        when().
            post("http://localhost:9876/address").
        then().
            assertThat().
            statusCode(200);
    }

    @Test
    public void deserializeJsonToAddress() {

        Address myAddress =

            given().
            when().
                get("http://localhost:9876/address").
                as(Address.class);

        Assert.assertEquals("Amsterdam", myAddress.getCity());
    }
}