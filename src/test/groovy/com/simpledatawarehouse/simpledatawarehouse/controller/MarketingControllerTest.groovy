package com.simpledatawarehouse.simpledatawarehouse.controller

import com.simpledatawarehouse.simpledatawarehouse.controller.request.Aggregations
import com.simpledatawarehouse.simpledatawarehouse.model.Marketing
import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll

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

    @Unroll
    def "clicks without anything given with different aggregators"(Aggregations aggregation, String result) {
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
            this.mockMvc.perform(post("/clicks/total/${aggregation}")
                    .content('{}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string(result))

        where:
            aggregation      | result
            Aggregations.SUM | "45"
            Aggregations.MIN | "3"
            Aggregations.MAX | "10"
            Aggregations.AVG | "7.5"
    }

    @Unroll
    def "total clicks returns a 400 response if any grouping value is passed"() {
        expect:
            this.mockMvc.perform(post("/${metric}/total/sum")
                .content('{"groupBy":"daily"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("groupBy is not supported"))
        where:
            metric << ["impressions", "clicks"]
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
            this.mockMvc.perform(post("/clicks/total/sum")
                    .content('{"dateFrom":"12/13/2019","dateTo": "12/13/2020"}')
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
        this.mockMvc.perform(post("/clicks/total/sum")
                .content('{"date":"12/13/2019"}')
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
            this.mockMvc.perform(post("/clicks/total/sum")
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
            this.mockMvc.perform(post("/clicks/total/sum")
                    .content('{"campaign":"cmp2"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string("8"))
    }

    def "total Click-Through Rate for a given date range, ordering by the same groupBy fields asc "() {
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
            this.mockMvc.perform(post("/ctr")
                    .content('{"dateFrom":"12/13/2019","dateTo": "12/14/2019","groupBy":"datasource,campaign"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("""[
                    {"datasource":"ds1","campaign":"cmp1","ctr":0.008333333333333333},
                    {"datasource":"ds1","campaign":"cmp2","ctr":0.0033333333333333335},
                    {"datasource":"ds2","campaign":"cmp1","ctr":0.0025}]""", true))
    }

    def "total Click-Through Rate returns a 400 if there is no groupBy or an invalid one"() {
        expect:
            this.mockMvc.perform(post("/ctr")
                .content('{}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("\"groupBy\"=\"datasource,campaign\" is needed for CTR"))
        where:
            content << ['{}','{"groupBy":"datasource"}','{"groupBy":"campaign"}','{"groupBy":"daily"}']
    }

    @Unroll
    def "total Click-Through Rate returns a 400 if there is a groupBy value that is invalid"() {
        expect:
            this.mockMvc.perform(post("/ctr")
                .content("""{"groupBy":"${groupBy}"}""")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string("'${groupBy}' is a invalid value for groupBy, valid values are: datasource, campaign, daily"))
        where:
            groupBy << ['invalidValue', 'datasource,invalidValue']
    }

    @Unroll
    def "total Click-Through Rate returns a 200 if there is a groupBy value allowing camel case and spaces"() {
        expect:
            this.mockMvc.perform(post("/ctr")
                .content("""{"groupBy":"${groupBy}"}""")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
        where:
            groupBy << ['Datasource,campaign', ' datasource,campaign ', ' campaign , Datasource ']
    }

    def "total Click-Through Rate for a given date"() {
        given: "ctr 20/4000 = 0.005"
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 20, 4000)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-15"), 10, 1000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-14"), 30, 2000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 10, 4000)
        and: "ctr of 10/3000 = 0.0033333333333333335"
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 10, 3000)

        expect:
            this.mockMvc.perform(post("/ctr")
                .content('{"date":"12/13/2019","groupBy":"datasource,campaign"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                    {"datasource":"ds1","campaign":"cmp1","ctr":0.005},
                    {"datasource":"ds1","campaign":"cmp2","ctr":0.0033333333333333335}]""", true))
    }

    def "total Click-Through Rate for a given datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 20, 4000)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-15"), 10, 1000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-14"), 30, 2000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 10, 4000)
        and:
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 10, 3000)

        expect:
            this.mockMvc.perform(post("/ctr")
                .content('{"datasource":"ds1","groupBy":"datasource,campaign"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                   {"ctr":0.008571428571428572,"datasource":"ds1","campaign":"cmp1"},
                   {"ctr":0.0033333333333333335,"datasource":"ds1","campaign":"cmp2"}]"""))
    }

    def "total Click-Through Rate for a given campaign"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 20, 4000)
        and:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-15"), 10, 1000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-14"), 30, 2000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 10, 4000)
        and:
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 10, 3000)

        expect:
            this.mockMvc.perform(post("/ctr")
                .content('{"campaign":"cmp1","groupBy":"datasource,campaign"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                   {"ctr":0.008571428571428572,"datasource":"ds1","campaign":"cmp1"},
                   {"ctr":0.0025,"datasource":"ds2","campaign":"cmp1"}]"""))
    }

    @Unroll
    def "total impressions without anything given with different aggregators"(Aggregations aggregation, String result) {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 8000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/total/${aggregation}")
                    .content('{}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json(result))
        where:
            aggregation      | result
            Aggregations.SUM | "16000"
            Aggregations.MIN | "4000"
            Aggregations.MAX | "8000"
            Aggregations.AVG | "5333.333333333333"
    }

    def "sum impressions given a date"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/total/sum")
                    .content('{"date":"12/13/2019"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("8000"))
    }

    def "sum impressions given a date range"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/total/sum")
                    .content('{"dateFrom":"12/13/2019","dateTo":"12/14/2019"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("12000"))
    }

    def "sum impressions given a datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/total/sum")
                    .content('{"datasource":"ds1"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("8000"))
    }

    def "sum impressions given a campaign"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 5000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 5000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/total/sum")
                    .content('{"campaign":"cmp2"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("10000"))
    }

    def "sum impressions over time daily without anything given"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-14"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                .content('{"groupBy": "daily"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":8000,"date":"2019-12-13"},
                {"total":4000,"date":"2019-12-14"}]"""))
    }

    def "sum impressions over time daily with a date range"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
        this.mockMvc.perform(post("/impressions/sum")
                .content('{"dateFrom":"12/13/2019","dateTo": "12/14/2019", "groupBy": "daily"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":8000,"date":"2019-12-13"},
                {"total":4000,"date":"2019-12-14"}]"""))
    }

    def "sum impressions over time daily with an specific date"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                    .content('{"date":"12/13/2019","groupBy":"daily"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("""[{"total":8000,"date":"2019-12-13"}]"""))
    }

    def "sum impressions over time daily for a given datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 4000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                    .content('{"datasource":"ds1","groupBy":"daily"}')
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json("""[{"total":4000,"date":"2019-12-13"}]"""))
    }

    def "sum impressions over time daily for a given campaign"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
        and:
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-14"), 1, 3000)
        and:
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-15"), 1, 4000)
            saveMarketing("ds2","cmp1", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                .content('{"campaign":"cmp2","groupBy": "daily"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":4000,"date":"2019-12-13"},
                {"total":3000,"date":"2019-12-14"}]"""))
    }

    def "sum impressions over time group by daily and campaign, it sorts by default in the same order as the groupBy"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                .content('{"groupBy": "daily,campaign,datasource"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":8000,"date":"2019-12-12","datasource":"ds2","campaign":"cmp2"},
                {"total":4000,"date":"2019-12-13","datasource":"ds1","campaign":"cmp1"},
                {"total":4000,"date":"2019-12-13","datasource":"ds1","campaign":"cmp2"}]""", true))
    }

    def "sum impressions over time group by daily and datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                .content('{"groupBy": "daily,datasource"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":8000,"date":"2019-12-12","datasource":"ds2"},
                {"total":8000,"date":"2019-12-13","datasource":"ds1"}]""", true))
    }

    def "sum impressions over time group by campaign and datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                .content('{"groupBy": "campaign,datasource"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":4000,"datasource":"ds1","campaign":"cmp1"},
                {"total":4000,"datasource":"ds1","campaign":"cmp2"},
                {"total":8000,"datasource":"ds2","campaign":"cmp2"}]""", true))
    }

    def "sum impressions over time group by campaign and date"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                .content('{"groupBy": "campaign,daily"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":4000,"date":"2019-12-13","campaign":"cmp1"},
                {"total":8000,"date":"2019-12-12","campaign":"cmp2"},
                {"total":4000,"date":"2019-12-13","campaign":"cmp2"}]""", true))
    }

    def "sum impressions over time group by datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)

        expect:
            this.mockMvc.perform(post("/impressions/sum")
                .content('{"groupBy": "datasource"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":8000,"datasource":"ds1"},
                {"total":8000,"datasource":"ds2"}]""", true))
    }

    def "max impressions over time group by daily and datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 1, 2000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 1, 5000)

        expect:
            this.mockMvc.perform(post("/impressions/max")
                .content('{"groupBy": "daily,datasource"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":5000,"date":"2019-12-12","datasource":"ds2"},
                {"total":4000,"date":"2019-12-13","datasource":"ds1"}]"""))
    }

    def "sum clicks over time group by daily and datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 2, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 3, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 4, 4000)

        expect:
            this.mockMvc.perform(post("/clicks/sum")
                .content('{"groupBy": "daily,datasource"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":7,"date":"2019-12-12","datasource":"ds2"},
                {"total":3,"date":"2019-12-13","datasource":"ds1"}]"""))
    }

    def "max clicks over time group by daily and datasource"() {
        given:
            saveMarketing("ds1","cmp1", LocalDate.parse("2019-12-13"), 1, 4000)
            saveMarketing("ds1","cmp2", LocalDate.parse("2019-12-13"), 2, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 3, 4000)
            saveMarketing("ds2","cmp2", LocalDate.parse("2019-12-12"), 4, 4000)

        expect:
            this.mockMvc.perform(post("/clicks/max")
                .content('{"groupBy": "daily,datasource"}')
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""[
                {"total":4,"date":"2019-12-12","datasource":"ds2"},
                {"total":2,"date":"2019-12-13","datasource":"ds1"}]"""))
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
