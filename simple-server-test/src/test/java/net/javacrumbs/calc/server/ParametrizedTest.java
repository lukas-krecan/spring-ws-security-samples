package net.javacrumbs.calc.server;

import java.util.Arrays;
import java.util.Collection;

import net.javacrumbs.springws.test.context.WsTestContextHolder;
import net.javacrumbs.springws.test.helper.WsTestHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.ws.context.MessageContext;

@RunWith(Parameterized.class)
// your endpoint configuration + Default helper config
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring-ws-servlet.xml", WsTestHelper.DEFAULT_CONFIG_PATH })
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
public class ParametrizedTest {

	@Autowired
	private WsTestHelper helper;
	
	private int a;
	
	private int b;
	
	private int result;

	private final TestContextManager testContextManager;
	
	
	public ParametrizedTest(int a, int b, int result) {
		this.testContextManager = new TestContextManager(getClass());
		this.a = a;
		this.b = b;
		this.result = result;
	}
	
	@Before
	public void injectDependencies() throws Throwable {
		this.testContextManager.prepareTestInstance(this);
	}


	@Parameters
	public static Collection<Integer[]> inputData() {
		return Arrays.asList(new Integer[][] { 
				{ 1, 2, 3}, 
				{ 0, 0, 0},
				{-1, 2, 1},
		});
	}
	
	@Test
	public void testAssertXPathContext() throws Exception {
		WsTestContextHolder.getTestContext().setAttribute("a", a);
		WsTestContextHolder.getTestContext().setAttribute("b", b);
		WsTestContextHolder.getTestContext().setAttribute("result", result);
		// simulates request coming to MessageDispatcherServlet
		MessageContext message = helper.receiveMessage("request-context-xslt.xml");
		// evaluate XPath
		helper.createMessageValidator(message.getResponse()).assertNotSoapFault().validate("xsd/calc.xsd").addNamespaceMapping("ns", "http://javacrumbs.net/calc").assertXPath("//ns:result=$context.result");
		
		WsTestContextHolder.getTestContext().clear();
	}

}