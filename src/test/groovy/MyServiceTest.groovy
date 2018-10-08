import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.mashape.unirest.http.Unirest
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

class MyServiceTest extends Specification {

    @Shared
    WireMockServer wireMockServer

    def setupSpec() {
        def port = 8181
        wireMockServer = new WireMockServer(new WireMockConfiguration().port(port))
        wireMockServer.start()
    }
    
    def afterSpec() {
        wireMockServer.shutdown()
    }

    def "A search returns the search results"() {
        given:
            wireMockServer.stubFor(get(urlEqualTo("/search")).willReturn(aResponse()
                    .withBody("""
                    {
                        "key": "value"
                    }""")
            ))

        when:
            def response = Unirest.get("http://localhost:8181/search").asJson().body

        then:
            response.getObject().getString("key") == "value"

    }

}