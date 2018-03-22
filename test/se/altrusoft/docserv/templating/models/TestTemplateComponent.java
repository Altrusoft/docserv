package se.altrusoft.docserv.templating.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestTemplateComponent {

	private String testString = "Hejsan";

	public TestTemplateComponent() {

	}

	public TestTemplateComponent(String testString) {
		this.testString = testString;
	}

	public String getTestString() {
		return testString;
	}

	public void setTestString(String testString) {
		this.testString = testString;
	}

}
