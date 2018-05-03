package endpoints.testsuite.client


import endpoints.testsuite.MuxEndpointTestApi

trait MuxEnpointTestSuite[T <: MuxEndpointTestApi] extends ClientTestBase[T] {

  import com.github.tomakehurst.wiremock.client.WireMock._
  def muxEnpointTestSuite() = {
    "Client interpreter" should {
      "return multiplex server response" in {

        wireMockServer.stubFor(get(urlEqualTo("/query"))
            .willReturn(aResponse()
              .withStatus(200)
              .withBody(""))
        )

        wireMockServer.stubFor(get(urlEqualTo("/query?id=toto"))
          .willReturn(aResponse()
            .withStatus(200)
            .withBody(""))
        )

      }
    }
  }

}
