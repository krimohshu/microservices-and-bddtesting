package com.microservices.user.test.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.microservices.user.test.context.TestContext;

@Slf4j
@RequiredArgsConstructor
public class TestHooks {

    private final TestContext testContext;

    @Before
    public void beforeScenario() {
        log.info("Starting test scenario");
        testContext.reset();
    }

    @After
    public void afterScenario() {
        log.info("Test scenario completed");
    }
}
