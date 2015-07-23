package net.javacrumbs.calc.server;

import net.javacrumbs.springws.test.context.WsTestContextHolder;
import net.javacrumbs.springws.test.helper.WsTestHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.context.MessageContext;

@RunWith(SpringJUnit4ClassRunner.class)
// your endpoint configuration + alternative helper config
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring-ws-servlet.xml",
		"classpath:spring/alternative-helper-config.xml" })
public class AlternativeEndpointTest {

	@Autowired
	private WsTestHelper helper;

	@Test
	public void testSimple() throws Exception {
		// simulates request coming to MessageDispatcherServlet
		MessageContext message = helper.receiveMessage("request1.xml");
		// compare response with control response
		helper.createMessageValidator(message.getResponse()).compare("response1.xml");
	}

	@Test
	public void testError() throws Exception {
		// simulates request coming to MessageDispatcherServlet
		MessageContext message = helper.receiveMessage("request-error.xml");
		// compare response with control response
		helper.createMessageValidator(message.getResponse()).compare("response-error.xml");
	}

	@Test
	public void testRequestTemplate() throws Exception {
		WsTestContextHolder.getTestContext().setAttribute("a", 1);
		WsTestContextHolder.getTestContext().setAttribute("b", 2);
		// simulates request coming to MessageDispatcherServlet
		MessageContext message = helper.receiveMessage("request-context.xml");
		// compare response with control response
		helper.createMessageValidator(message.getResponse()).compare("response1.xml");
	}
	
	@Test
	public void testResponseTemplate() throws Exception {
		WsTestContextHolder.getTestContext().setAttribute("a", 1);
		WsTestContextHolder.getTestContext().setAttribute("b", 2);
		WsTestContextHolder.getTestContext().setAttribute("result", 3);
		// simulates request coming to MessageDispatcherServlet
		MessageContext message = helper.receiveMessage("request-context.xml");
		// compare response with control response
		helper.createMessageValidator( message.getResponse()).assertNotSoapFault().compare("response-context.xml");
		
		WsTestContextHolder.getTestContext().clear();
	}
}