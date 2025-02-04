package org.example

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class Assignment extends Simulation {

	val httpProtocol = http
		.baseUrl("https://petstore.octoperf.com")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36")

	val headers_0 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"accept-encoding" -> "gzip, deflate, br, zstd",
		"accept-language" -> "en-US,en;q=0.9",
		"priority" -> "u=0, i",
		"sec-ch-ua" -> """Not A(Brand";v="8", "Chromium";v="132", "Google Chrome";v="132""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-ch-ua-platform" -> "Windows",
		"sec-fetch-dest" -> "document",
		"sec-fetch-mode" -> "navigate",
		"sec-fetch-site" -> "none",
		"sec-fetch-user" -> "?1",
		"upgrade-insecure-requests" -> "1")

	val headers_1 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"accept-encoding" -> "gzip, deflate, br, zstd",
		"accept-language" -> "en-US,en;q=0.9",
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
		"accept-encoding" -> "gzip, deflate, br, zstd",
		"accept-language" -> "en-US,en;q=0.9",
		"priority" -> "u=2, i",
		"sec-ch-ua" -> """Not A(Brand";v="8", "Chromium";v="132", "Google Chrome";v="132""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-ch-ua-platform" -> "Windows",
		"sec-fetch-dest" -> "image",
		"sec-fetch-mode" -> "no-cors",
		"sec-fetch-site" -> "same-origin")

	val headers_4 = Map(
		"sec-ch-ua" -> """Not A(Brand";v="8", "Chromium";v="132", "Google Chrome";v="132""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-ch-ua-platform" -> "Windows")

	val headers_5 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
		"accept-encoding" -> "gzip, deflate, br, zstd",
		"accept-language" -> "en-US,en;q=0.9",
		"origin" -> "https://petstore.octoperf.com",
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


	val scn = scenario("Testing")
		.feed(CategoryList_feed)
		.feed(UserCredentials)
		.feed(Quantity)
		.feed(CardDetails)

 		.group("SC01_T01_Launch") {
			exec(http("Launch")
				.get("/")
				.headers(headers_0)
				.check(status.is(200))
			)
		}

		.pause(Thinktime)

		.group("SC01_T02_Homepage") {
			exec(http("Homepage_1")
			.get("/actions/Catalog.action")
			.headers(headers_1)
			.check(substring("WelcomeContent"))
			.check(regex("""jsessionid=(.+?)\?viewCategory""").find.saveAs("c_jsessionID"))
			.check(status.is(200))
			.resources(http("Homepage_2")
			.get("/images/logo-topbar.svg")
			.headers(headers_2)))
			}

		.pause(Thinktime)

		.group("SC02_T03_ClickOnSignIn") {
			exec(http("CLickOnSign_1")
				.get("/actions/Account.action;jsessionid=${c_jsessionID}?signonForm=")
				.headers(headers_1)
				.check(status.is(200))
				.check(substring("Sign In"))
				.resources(http("CLickOnSign_2")
					.get("/images/logo-topbar.svg")
					.headers(headers_4)))
		}

		.pause(Thinktime)

		.group("SC02_T04_EnterDetails") {
		exec(http("EnterDetails_1")
				.post("/actions/Account.action")
				.headers(headers_5)
				.formParam("username", "${P_userName}")
				.formParam("password", "${P_passWord}")
				.formParam("signon", "Login")
				.formParam("_sourcePage", "2gYNsN5vy_8JZoaStgDPLSQLDSPzn5FjijveVbnwxQDGnGC-RnJaJUXE5f48WfvkJNBljhfHcd3rfoVuLLStUDcy1ieU0wM0lHhegdQ4xyU=")
				.formParam("__fp", "NUP4aNBi2CxrbVvGoAyGUaRSeF4GJW_r90Itn-8oHoHRoMeJSJDqvkVuojmenqS8")
				.resources(http("EnterDetails_2")
					.get("/images/logo-topbar.svg")
					.headers(headers_4)))
		}

		.pause(Thinktime)

		.forever() {

		 group("SC01_T05_SelectCategory") {
				exec(http("SelectCategory_1")
					.get("/actions/Catalog.action?viewCategory=&categoryId=${P_CategoryList}")
					.headers(headers_1)
					.check(regex("""viewProduct=&amp;productId=(.*?)">""").findRandom.saveAs("c_productID"))
					.check(status.is(200))
					.check(substring("Catalog"))
					.resources(http("SelectCategory_2")
						.get("/images/logo-topbar.svg")
						.headers(headers_4)))
			}

				.pause(Thinktime)

				.exec(session => {
					val ProductId = session("c_productID").as[String]
					println(s"Extracted ProductId: $ProductId")
					session
				})


				.group("SC01_T06_SelectPDP") {
					exec(http("SelectPDP_1")
						.get("/actions/Catalog.action?viewProduct=&productId=${c_productID}")
						.headers(headers_1)
						.check(regex("""addItemToCart=&amp;workingItemId=(.*?)"""").findRandom.saveAs("c_cartItem"))
						.check(status.is(200))
						.check(substring("categoryId"))
						.resources(http("SelectPDP_2")
							.get("/images/logo-topbar.svg")
							.headers(headers_4)))
				}

				.exec(session => {
					// Access the session variable and print it
					val cartItem = session("c_cartItem").as[String]
					println(s"Extracted CartItem: $cartItem") // This will print the extracted productID
					session // Don't forget to return the session
				})

				.pause(Thinktime)

				.group("SC01_T07_AddToCart") {
					exec(http("AddToCart_1")
						.get("/actions/Cart.action?addItemToCart=&workingItemId=${c_cartItem}")
						.headers(headers_1)
						.resources(http("AddToCart_2")
							.get("/images/logo-topbar.svg")
							.headers(headers_4)))
				}

				.pause(Thinktime)

				.group("SC01_T08_updateQuantity") {
					exec(http("updateQuantity_1")
						.post("/actions/Cart.action")
						.headers(headers_5)
						.check(status.is(200))
						.check(substring("updateCartQuantities"))
						.formParam("EST-4", "2")
						.formParam("updateCartQuantities", "${P_Quantity}")
						.formParam("_sourcePage", "rD-7snsY24BrQZtFHriqfBjYh5dejrYIV5qhS3wBxTFaUu-02q6N48T6oQy02G86ZD9hQZk87CKYCcEdTsFvGziVKFjUPTQG")
						.formParam("__fp", "DmV6DTOpIZOpM3OReU7ZnGgoHi9zGV104e81hUEoLX15EuuQ-X_hu5SXlTy2Hs11")
						.resources(http("updateQuantity_2")
							.get("/images/logo-topbar.svg")
							.headers(headers_4)))
				}

				.pause(Thinktime)

				.group("SC01_T09_ProceedToCheckout") {
					exec(http("ProceedToCheckout_1")
						.get("/actions/Order.action?newOrderForm=")
						.headers(headers_1)
						.check(status.is(200))
						.resources(http("ProceedToCheckout_2")
							.get("/images/logo-topbar.svg")
							.headers(headers_4)))
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
						.formParam("_sourcePage", "jQDZvy5PqsW4oyWDvje3bb_lb_Vmmi62V2fAZqCRJmQuOxedFrqMIFNgN_WWJlR8PgBKSGyXZRZXcWIlL5Fdi6qvVVABwJrdLPnSyI18Hic=")
						.formParam("__fp", "st6WidSJ-GZgHPzXY9IidAZhV97aFMJtnjK9tKfDW5weyh_Dm7Nvr7MlmQLFIUQrYFqN6WZKKd08iy_0mdPQno6pvL3PJ_E2N-U_rg90vhqyg8Ojw5Tcqg==")
						.resources(http("EnterCardDetails_2")
							.get("/images/logo-topbar.svg")
							.headers(headers_4)))
				}

				.pause(Thinktime)

				.group("SC01_T11_ClickOnConfirm") {
					exec(http("ClickOnConfirm_1")
						.get("/actions/Order.action?newOrder=&confirmed=true")
						.headers(headers_1)
						.check(status.is(200))
						.resources(http("ClickOnConfirm_2")
							.get("/images/logo-topbar.svg")
							.headers(headers_4)))
				}

				.pause(Thinktime)

				.group("SC01_T12_Return to menu") {
					exec(http("Return to menu_1")
						.get("/actions/Catalog.action")
						.headers(headers_1)
						.resources(http("Return to menu_2")
							.get("/images/logo-topbar.svg")
							.headers(headers_4)))
				}
		}

		.pause(Thinktime)

		.group("SC01_T13_LogOut") {
		exec(http("LogOut_1")
				.get("/actions/Account.action?signoff=")
				.headers(headers_1)
				.resources(http("LogOut_2")
					.get("/images/logo-topbar.svg")
					.headers(headers_4)))
		}

		.pause(Thinktime)

//	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

	setUp(
		scn.inject(
			rampUsers(5) during (30.seconds),
			nothingFor(30.seconds) ,
			rampUsers(5) during (5.minutes),
			rampUsers(5) during (5.minutes),
			rampUsers(5) during (5.minutes),
			rampUsers(5) during (5.minutes),
			rampUsers(5) during (5.minutes)
		).protocols(httpProtocol)
	)

}