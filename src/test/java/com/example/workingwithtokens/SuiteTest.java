package com.example.workingwithtokens;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        StandardRegistrationTests.class,
        StandardLoginTests.class,
        VkAuthTest.class,
        GoogleAuthTest.class
} )
public class SuiteTest {
}
