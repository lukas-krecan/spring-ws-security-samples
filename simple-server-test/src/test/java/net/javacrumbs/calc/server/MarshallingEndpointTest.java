package net.javacrumbs.calc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.javacrumbs.calc.model.PlusRequest;
import net.javacrumbs.calc.model.PlusResponse;
import net.javacrumbs.springws.test.helper.MessageValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceOperations;
import org.springframework.ws.soap.client.SoapFaultClientException;

@RunWith(SpringJUnit4ClassRunner.class)
// your endpoint configuration + extended config
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring-ws-servlet.xml",
		"classpath:spring/marshalling-helper-config.xml" })
public class MarshallingEndpointTest {

	@Autowired
	private WebServiceOperations wsTemplate;
	
	@Test
	public void testSimple() throws Exception {
		PlusRequest request = new PlusRequest();
		request.setA(1);
		request.setB(2);
		
		PlusResponse response = (PlusResponse) wsTemplate.marshalSendAndReceive(request);
		// assert that response is not fault
		assertEquals(3, response.getResult());
	}
	@Test
	public void testError() throws Exception {
		PlusRequest request = new PlusRequest();
		request.setA(Integer.MAX_VALUE);
		request.setB(2);
		
		try
		{
			wsTemplate.marshalSendAndReceive(request);
			fail("Exception expected");
		}
		catch(SoapFaultClientException e)
		{
			assertEquals("Values are too big.",e.getSoapFault().getFaultStringOrReason());
			//or an alternative error validation 
			new MessageValidator(e.getWebServiceMessage()).assertSoapFault().assertFaultStringOrReason("Values are too big.");
		}

	}
}