package net.javacrumbs.calc.server;

import net.javacrumbs.springws.test.helper.WsTestHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.context.MessageContext;

@RunWith(SpringJUnit4ClassRunner.class)
// your endpoint configuration + secured helper config
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/secured-spring-ws-servlet.xml",
		"classpath:spring/secured-helper-config.xml" })
public class SecuredEndpointTest {

	@Autowired
	private WsTestHelper helper;

	@Test
	public void testSimple() throws Exception {
		// simulates request coming to MessageDispatcherServlet
		MessageContext message = helper.receiveMessage("request1.xml");
		// assert that response is not fault
		helper.createMessageValidator(message.getResponse()).assertNotSoapFault();
	}
}