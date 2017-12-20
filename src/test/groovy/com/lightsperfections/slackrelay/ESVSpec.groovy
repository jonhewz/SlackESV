package net.averagehero.slackrelay

import groovyx.net.http.RESTClient
import net.averagehero.slackesv.Application
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import spock.lang.Specification

import static groovyx.net.http.ContentType.URLENC

/**
 * Created with IntelliJ IDEA.
 * User: jhughes
 * Date: 11/18/15
 * Time: 2:31 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes=Application.class)
public class ESVSpec extends Specification {

    @LocalServerPort
    private int port;

    @Test
    def "GET is an invalid request method"() {
        setup:
        def client = new RESTClient("http://localhost:$port/")
        client.handler.failure = client.handler.success
        when:
        def resp = client.get([path: 'esv', query: [input: 'phil']])
        then:
        with(resp) {
            status == 405
        }
    }

    @Test
    def "POST with all required params returns success"() {
        setup:
        def client = new RESTClient("http://localhost:$port/")
        client.handler.failure = client.handler.success
        def paramMap = [token       : 'TEST',
                        text        : 'text',
                        response_url: 'http://localhost']

        when:
        def resp = client.post(
                path: 'esv',
                requestContentType: URLENC,
                body: paramMap
        )
        then:
        with(resp) {
            status == 200
        }
    }

    @Test
    def "Invalid token returns unauthorized"() {
        setup:
        def client = new RESTClient("http://localhost:$port/")
        client.handler.failure = client.handler.success
        def paramMap = [token       : 'INVALIDTOKEN',
                        text        : 'text',
                        response_url: 'http://localhost']

        when:
        def resp = client.post(
                path: 'esv',
                requestContentType: URLENC,
                body: paramMap
        )
        then:
        with(resp) {
            status == 401
        }
    }

    @Test
    def "POST with missing param returns 400"() {
        setup:
        def paramMap = [token  : 'TEST',
                        text   : 'text']

        def client = new RESTClient("http://localhost:$port/")
        client.handler.failure = client.handler.success
        when:
        def resp = client.post(
                path: 'esv',
                requestContentType: URLENC,
                body: paramMap
        )
        then:
        with(resp) {
            status == 400
        }
    }


    @Test
    def "ESV with no params defaults to HELP"() {
        setup:
        def paramMap = [token       : 'TEST',
                        text        : ' ',
                        response_url: 'http://localhost']

        def client = new RESTClient("http://localhost:$port/")
        client.handler.failure = client.handler.success
        when:
        def resp = client.post(
                path: 'esv',
                body: paramMap,
                requestContentType: URLENC
        )
        then:
        with(resp) {
            status == 200
            data.text.contains('ESV Help')
        }
    }
}
