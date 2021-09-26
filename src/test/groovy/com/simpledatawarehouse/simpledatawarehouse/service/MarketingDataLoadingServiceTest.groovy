package com.simpledatawarehouse.simpledatawarehouse.service

import com.github.tomakehurst.wiremock.client.WireMock
import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse

@DataJpaTest
@AutoConfigureWireMock(port = 9000)
class MarketingDataLoadingServiceTest extends Specification {

    MarketingDataLoadingService marketingDataLoadingService

    @Autowired
    MarketingRepository marketingRepository

    void setup() {
        marketingDataLoadingService= new MarketingDataLoadingService(marketingRepository, "http://localhost:9000/file.csv", true)
    }

    def "loads the data"() {
        given:
            File file = new File("src/test/resources/data/marketing-data.csv")
            WireMock.stubFor(WireMock.get ( WireMock.urlEqualTo("/file.csv")).willReturn(aResponse()
                .withHeader("Content-Type", "text/csv").withBody(file.text)))
        when:
            marketingDataLoadingService.loadMarketingData()
        then:
            marketingRepository.count() == 23198

    }
}
