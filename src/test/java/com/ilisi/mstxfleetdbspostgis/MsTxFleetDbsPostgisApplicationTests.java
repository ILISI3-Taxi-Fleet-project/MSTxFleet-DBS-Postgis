package com.ilisi.mstxfleetdbspostgis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsTxFleetDbsPostgisApplicationTests {

    /**this causes problem in prod profile when building project, it tries to connect to postgresql while postgres
    has not been started yet
    */
    @Test
    void contextLoads() {
    }

}
