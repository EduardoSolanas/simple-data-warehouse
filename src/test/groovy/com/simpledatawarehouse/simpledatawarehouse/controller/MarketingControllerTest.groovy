package com.simpledatawarehouse.simpledatawarehouse.controller

import com.simpledatawarehouse.simpledatawarehouse.model.Marketing
import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class MarketingControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private MarketingRepository marketingRepository

    void setup() {
        marketingRepository.deleteAll()
    }

    def "total clicks without anything given"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-12"), 10)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-12"),3)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-14"),7)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-13"), 5)
        expect:
            this.mockMvc.perform(post("/marketing/clicks/total")
                    .content('{}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string("45"))
    }

    def "total clicks for a given date range"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-12"), 10)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-12"),3)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-14"),7)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-13"), 5)
        expect:
            this.mockMvc.perform(post("/marketing/clicks/total")
                    .content('{"dateFrom":"12/13/19","dateTo": "12/13/20"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string("35"))
    }

    def "total clicks for a given date"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-12"), 10)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-12"),3)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-14"),7)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-13"), 5)
        expect:
        this.mockMvc.perform(post("/marketing/clicks/total")
                .content('{"date":"12/13/19"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("15"))
    }

    def "total clicks for a given datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-12"), 10)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-12"),3)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-14"),7)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-13"), 5)
        expect:
            this.mockMvc.perform(post("/marketing/clicks/total")
                    .content('{"datasource":"ds1"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string("40"))
    }

    def "total clicks for a given campaign"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-13"), 10)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-12"), 10)
        and:
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-12"),3)
            saveMarketing("ds1","cmp1", LocalDate.parse("2020-12-14"),7)
        and:
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 5)
        expect:
            this.mockMvc.perform(post("/marketing/clicks/total")
                    .content('{"campaign":"cmp2"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string("8"))
    }

    def "total clicks over time for a given date range "() {
        given: "ctr 50/6000 = 0.008333333333333333"
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 20, 4000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-14"), 30, 2000)
        and: "ctr 10/4000=0.0025"
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 10, 4000)
        and: "ctr of 10/3000 = 0.0033333333333333335"
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-14"), 10, 3000)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-15"), 10, 1000)

        expect:
            this.mockMvc.perform(post("/marketing/ctr")
                    .content('{"dateFrom":"12/13/19","dateTo": "12/14/19"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("""[
                    {"datasource":"ds1","campaign":"cmp1","ctr":0.008333333333333333},
                    {"datasource":"ds1","campaign":"cmp2","ctr":0.0033333333333333335},
                    {"datasource":"ds2","campaign":"cmp1","ctr":0.0025}]"""))
    }

    def "total clicks over time for a given date"() {
        given: "ctr 20/4000 = 0.005"
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 20, 4000)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-15"), 10, 1000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-14"), 30, 2000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 10, 4000)
        and: "ctr of 10/3000 = 0.0033333333333333335"
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 10, 3000)

        expect:
            this.mockMvc.perform(post("/marketing/ctr")
                .content('{"date":"12/13/19"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                    {"datasource":"ds1","campaign":"cmp1","ctr":0.005},
                    {"datasource":"ds1","campaign":"cmp2","ctr":0.0033333333333333335}]"""))
    }

    def "total impressions over time daily without anything given"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/marketing/impressions/total")
                .content('{}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"impressions":8000,"date":"2019-12-13"},
                {"impressions":4000,"date":"2019-12-14"}]"""))
    }

    def "total impressions over time daily with a date range"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
        this.mockMvc.perform(post("/marketing/impressions/total")
                .content('{"dateFrom":"12/13/19","dateTo": "12/14/19"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"impressions":8000,"date":"2019-12-13"},
                {"impressions":4000,"date":"2019-12-14"}]"""))
    }

    def "total impressions over time daily with an specific date"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/marketing/impressions/total")
                    .content('{"date":"12/13/19"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("""[{"impressions":8000,"date":"2019-12-13"}]"""))
    }

    def "total impressions over time daily for a given datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/marketing/impressions/total")
                    .content('{"datasource":"ds1"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("""[{"impressions":4000,"date":"2019-12-13"}]"""))
    }

    def "total impressions over time daily for a given campaign"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
        and:
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 3000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/marketing/impressions/total")
                .content('{"campaign":"cmp2"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"impressions":4000,"date":"2019-12-13"},
                {"impressions":3000,"date":"2019-12-14"}]"""))
    }


    def saveMarketing(datasource,campaign, date, clicks = 0, impressions = 0) {
        def mk = new Marketing()
        mk.setDatasource(datasource)
        mk.setCampaign(campaign)
        mk.setDaily(date)
        mk.setClicks(clicks)
        mk.setImpressions(impressions)
        marketingRepository.save(mk)
    }
}
