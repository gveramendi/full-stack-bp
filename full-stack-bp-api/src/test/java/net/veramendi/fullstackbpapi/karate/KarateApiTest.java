package net.veramendi.fullstackbpapi.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KarateApiTest {

    @LocalServerPort
    int port;

    @Karate.Test
    Karate runApiFeatures() {
        System.setProperty("karate.server.port", String.valueOf(port));
        return Karate.run("classpath:karate").relativeTo(getClass());
    }
}
