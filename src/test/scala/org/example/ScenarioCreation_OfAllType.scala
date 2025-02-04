package org.example

import scala.concurrent.duration._
import scala.concurrent._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ScenarioCreation_OfAllType extends Simulation {

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  val httpProtocol = http
    .baseUrl("https://petstore.octoperf.com")
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.9")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36")


  val headers_1 = Map(
    "pragma" -> "no-cache",
    "priority" -> "u=0, i",
    "sec-ch-ua" -> """Not A(Brand";v="8", "Chromium";v="132", "Google Chrome";v="132""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "Windows",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val headers_2 = Map(
    "accept" -> "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8",
    "pragma" -> "no-cache",
    "priority" -> "u=2, i",
    "sec-ch-ua" -> """Not A(Brand";v="8", "Chromium";v="132", "Google Chrome";v="132""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "Windows",
    "sec-fetch-dest" -> "image",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "same-origin")

  val headers_5 = Map(
    "origin" -> "https://petstore.octoperf.com",
    "pragma" -> "no-cache",
    "priority" -> "u=0, i",
    "sec-ch-ua" -> """Not A(Brand";v="8", "Chromium";v="132", "Google Chrome";v="132""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "Windows",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val Thinktime = 30.milliseconds;
  val CategoryList_feed = csv("src/test/resources/Data/CategoryList.csv").random
  val UserCredentials = csv("src/test/resources/Data/Credentials.csv").random
  val Quantity = csv("src/test/resources/Data/Quantity.csv").random
  val CardDetails = csv("src/test/resources/Data/CardDetails.csv").random

  def testDuration: Int = getProperty("TEST_DURATION", "390").toInt

  val scn = scenario("RecordedSimulation")
    .feed(CategoryList_feed)
    .feed(UserCredentials)
    .feed(Quantity)
    .feed(CardDetails)
    //.forever() {//it acts as loop with infinite iteration
    //	 .repeat(10){ //it acts as loop with desire number of iteration

    .during(testDuration.seconds) {
      group("SC01_T01_Launch") {
        exec(http("Launch")
          .get("/")
          .headers(headers_1)
          .check(status.is(200))
        )
      }

        .pause(Thinktime)

      group("SC01_T02_EnterTheStore") {
          exec(http("EnterTheStore")
            .get("/actions/Catalog.action")
            .headers(headers_1)
            .check(substring("WelcomeContent"))
            .check(status.is(200))
          )
      }
        .pause(Thinktime)

        .group("SC01_T03_ClickOnSignIn") {
          exec(http("Signin_1")
            .get("/actions/Account.action?signonForm=")
            .headers(headers_1)
            .resources(http("Signin_2")
              .get("/images/logo-topbar.svg")
              .headers(headers_2))
            .check(status.is(200))
            .check(substring("Sign In"))
          )
        }
        .pause(Thinktime)

        .group("SC01_T04_EnterDetails") {
          exec(http("EnterDetails_1")
            .post("/actions/Account.action")
            .headers(headers_5)
            .formParam("username", "${P_userName}")
            .formParam("password", "${P_passWord}")
            .formParam("signon", "Login")
            .formParam("_sourcePage", "96RTD1KSoCXaOruokox2Ke_9eAijYi3huGLWcmziXjMXVuYJwXkg2PqBhP3FNxPfMZxrkCsYotosNh7kZ0_lwcvB0Ab4NXkpmZOJkLHaisU=")
            .formParam("__fp", "5JHPBNL81_nciZ5W6Zu8AO_o_x258_hhiHYgogY2V7sgJ1pC41vAK_b0KjQYcSBH")
            .resources(http("EnterDetails_2")
              .get("/images/logo-topbar.svg")
              .headers(headers_2))
            .check(status.is(200))
          )
        }

//        .forever() {

          group("SC01_T05_SelectCategory") {
            exec(http("SelectCategory_1")
              .get("/actions/Catalog.action?viewCategory=&categoryId=${P_CategoryList}")
              .headers(headers_1)
              .check(regex("""viewProduct=&amp;productId=(.*?)">""").findRandom.saveAs("c_productID")) //Corelation
              .resources(http("request_4")
                .get("/images/logo-topbar.svg")
                .headers(headers_2))
              .check(status.is(200))
              .check(substring("Catalog"))
            )
          }
            .exec(session => {
              val ProductId = session("c_productID").as[String]
              println(s"Extracted ProductId: $ProductId")
              session
            })


            .group("SC01_T06_SelectPDP") {
              exec(http("SelectPDP_1")
                .get("/actions/Catalog.action?viewProduct=&productId=${c_productID}")
                .headers(headers_1)
                .check(regex("""addItemToCart=&amp;workingItemId=(.*?)"""").findRandom.saveAs("c_cartItem")) //Corelation
                .resources(http("request_6")
                  .get("/images/logo-topbar.svg")
                  .headers(headers_2))
                .check(status.is(200))
                .check(substring("categoryId"))
              )
            }
            .exec(session => {
              // Access the session variable and print it
              val cartItem = session("c_cartItem").as[String]
              println(s"Extracted CartItem: $cartItem")  // This will print the extracted productID
              session  // Don't forget to return the session
            })

            .pause(Thinktime)
            .group("SC01_T07_AddToCart") {
              exec(http("AddToCart)_1")
                .get("/actions/Cart.action?addItemToCart=&workingItemId=${c_cartItem}")
                .headers(headers_1)
                .resources(http("request_8")
                  .get("/images/logo-topbar.svg")
                  .headers(headers_2))
                .check(status.is(200))
                .check(substring("Cart"))
              )

            }
            .pause(Thinktime)
            .group("SC01_T08_updateQuantity") {
              exec(http("updateQuantity_1")
                .post("/actions/Cart.action")
                .headers(headers_1)
                .formParam("EST-1", "${P_Quantity}")
                .formParam("updateCartQuantities", "Update Cart")
                .formParam("_sourcePage", "TeKXihzNPIymgmibUeAfNfFDoRfI5ni2FpybHYRaJhgooXjNShg073qOdBgX-tbTCkeo-HTwkwi3ka8f9a8xXuWFZRotdWkI")
                .formParam("__fp", "GdaeHbIirvIf0ZMNpDMT8Zba2efR6jlg-ffteUT6h1wfGsdE7UNRBFrJHe4BiB2T")
                .check(status.is(200))
                .check(substring("updateCartQuantities"))
              )

            }
            .pause(Thinktime)
            .group("SC01_T09_ProceedToCheckout") {
              exec(http("ProceedToCheckout_1")
                .get("/actions/Order.action?newOrderForm=")
                .headers(headers_1)
                .resources(http("request_10")
                  .get("/images/logo-topbar.svg")
                  .headers(headers_2))
                .check(status.is(200))
              )

            }
            .pause(Thinktime)
            .group("SC01_T10_EnterCardDetails") {
              exec(http("EnterCardDetails_1")
                .post("/actions/Order.action")
                .headers(headers_5)
                .formParam("order.cardType", "${P_cardType}")
                .formParam("order.creditCard", "${P_creditCard}")
                .formParam("order.expiryDate", "${P_expiryDate}")
                .formParam("order.billToFirstName", "${P_billToFirstName}")
                .formParam("order.billToLastName", "${P_billToLastName}")
                .formParam("order.billAddress1", "${P_billAddress1}")
                .formParam("order.billAddress2", "${P_billAddress2}")
                .formParam("order.billCity", "${P_billCity}")
                .formParam("order.billState", "${P_billState}")
                .formParam("order.billZip", "${P_billZip}")
                .formParam("order.billCountry", "${P_billCountry}")
                .formParam("newOrder", "Continue")
                .formParam("_sourcePage", "IcrHU7EvnN0I6iacL5ScgtYmNk7nte73lA7Ext0TszQjaOtD_NC9jZqdUc1IC2m1CucK6amRLjuZNJxYipYsHeQmFzAq3nyZ1rX1d4fbzXA=")
                .formParam("__fp", "oIgfRJS3ByIIWDX2IE4l9pR-ASYfkTUZYVddTueERh8fAYwmLiceAoQifj_muHlMfYsccOK3laLzAsFxbKOObooi0dw5-l1Jj4wHi7t3JGDTcz6fK5xcHQ==")
                .resources(http("request_16")
                  .get("/images/logo-topbar.svg")
                  .headers(headers_2))
              )

            }

            .pause(Thinktime)
            .group("SC01_T11_ClickOnConfirm") {
              exec(http("ClickOnConfirm_1")
                .get("/actions/Order.action?newOrder=&confirmed=true")
                .headers(headers_1)
                .resources(http("request_18")
                  .get("/images/logo-topbar.svg")
                  .headers(headers_2))
                .check(status.is(200))
                .check(substring("Payment Details"))
                .check(status.saveAs("responseStatus"))
              )

                .exec(session => {
                  			val statusCode = session("responseStatus").as[Int]

                  			if (statusCode != 200) {
                  				println("Status $statusCode received, moving to next iteration...")
                  				session.markAsFailed
                  			} else {
                  				println("Status $statusCode received, continuing execution...")
                  				session
                  			}
                  		})

            }

            .pause(Thinktime)
            .group("SC01_T12_Return to menu") {
              exec(http("Return to menu_1")
                .get("/actions/Catalog.action")
                .headers(headers_1)
                .resources(http("request_20")
                  .get("/images/logo-topbar.svg")
                  .headers(headers_2))
                .check(status.is(200))
              )

            }

            .pause(Thinktime)
            .group("SC01_T13_LogOut") {
              exec(http("LogOut_1")
                .get("/actions/Account.action?signoff=")
                .headers(headers_1)
                .resources(http("request_22")
                  .get("/images/logo-topbar.svg")
                  .headers(headers_2))
                .check(status.is(200))
              )

//            }
            .pause(Thinktime)
     }
    }

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol) //Single User Single Iteration Scenario

  //Peak Load test with rampup and rampdown scenario

//  	setUp(
//  		scn.
//  			inject(rampConcurrentUsers(0).to(10).during(30.seconds),
//  				     rampConcurrentUsers(10).to(0).during(30.seconds)
//        )
//        .protocols(httpProtocol)
//    )
 }