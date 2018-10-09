import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.json.JSONObject
import org.poc.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class MyServiceTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate

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
            wireMockServer.stubFor(get(urlEqualTo("/foo")).willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("""{
                        "inner": "value"
                    }""")
            ))

        when:
            def response = restTemplate.getForEntity("/foo", String.class)

        then:
            response.statusCodeValue == 200
            new JSONObject(response.body).getString("key") == "value"

    }

}