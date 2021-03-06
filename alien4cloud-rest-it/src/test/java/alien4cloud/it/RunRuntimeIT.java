package alien4cloud.it;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:alien/rest/runtime" }, format = { "pretty", "html:target/cucumber/runtime",
        "json:target/cucumber/cucumber-runtime.json" })
public class RunRuntimeIT {
}
