package com.omnixys.payment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PaymentApplicationTests {

	@BeforeAll
    protected static void setup() {
        new Env();
    }

	@Test
	void contextLoads() {
	}

}
