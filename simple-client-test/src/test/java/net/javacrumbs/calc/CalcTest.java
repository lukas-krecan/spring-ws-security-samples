package net.javacrumbs.calc;

import static org.junit.Assert.assertEquals;
import net.javacrumbs.springws.test.simple.WsMockControl;
import net.javacrumbs.springws.test.simple.annotation.WsMockControlTestExecutionListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.ws.soap.client.SoapFaultClientException;

@RunWith(SpringJUnit4ClassRunner.class)
//load your standard config
@ContextConfiguration(locations={"classpath:client-config.xml"})
//Add the listener (DirtiesContextTestExecutionListener.class,  TransactionalTestExecutionListener.class might be needed if @DirtiesContext or @Transactional is used.
@TestExecutionListeners({WsMockControlTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
public class CalcTest {
    @Autowired
    private Calc calc;
    
    //inject mock control
    @Autowired
    private WsMockControl mockControl;

	@Test
	public void testSimple()
	{
		mockControl.returnResponse("response1.xml");

		int result = calc.plus(1, 2);
		assertEquals(3, result);
		
		mockControl.verify();
	}
	
	@Test
	public void testVerifyRequest()
	{
		mockControl.expectRequest("request1.xml").returnResponse("response1.xml");
		
		int result = calc.plus(1, 2);
		assertEquals(3, result);
		
		mockControl.verify();
	}
	@Test
	public void testSchema()
	{
		mockControl.validateSchema("xsd/calc.xsd").expectRequest("request1.xml").returnResponse("response1.xml");
		
		int result = calc.plus(1, 2);
		assertEquals(3, result);
		
		mockControl.verify();
	}
	@Test
	public void testIgnore()
	{
		mockControl.validateSchema("xsd/calc.xsd").expectRequest("request-ignore.xml").returnResponse("response2.xml");
		
		int result = calc.plus(2, 3);
		assertEquals(5, result);
		
		mockControl.verify();
	}
	@Test
	public void testMultiple()
	{
		mockControl.validateSchema("xsd/calc.xsd").expectRequest("request-ignore.xml").atLeastOnce().returnResponse("response2.xml").returnResponse("response3.xml");
		
		assertEquals(5, calc.plus(2, 3));
		assertEquals(8, calc.plus(3, 5));
				
		mockControl.verify();
	}
	@Test
	public void testStrange()
	{
		mockControl
		    .expectRequest("request1.xml").returnResponse("response1.xml")
			.expectRequest("request-ignore.xml").returnResponse("response2.xml")
			.expectRequest("request1.xml").returnResponse("response1.xml");
		
		assertEquals(3, calc.plus(1, 2));
		assertEquals(5, calc.plus(2, 3));
		assertEquals(3, calc.plus(1, 2));
		
		mockControl.verify();
	}
	@Test
	public void testTemplate()
	{
		mockControl.validateSchema("xsd/calc.xsd").expectRequest("request-ignore.xml").returnResponse("response-template.xml");
		
		int result = calc.plus(2, 3);
		assertEquals(5, result);
		
		mockControl.verify();
	}
	
	@Test
	public void testMultipleTemplate()
	{
		mockControl.validateSchema("xsd/calc.xsd").expectRequest("request-ignore.xml").returnResponse("response-template.xml").atLeastOnce();
		
		assertEquals(5, calc.plus(2, 3));
		assertEquals(8, calc.plus(3, 5));
		
		mockControl.verify();
	}

	@Test
	public void testContext()
	{
		mockControl
			.setTestContextAttribute("a", 1)
			.setTestContextAttribute("b", 4)
			.useFreeMarkerTemplateProcessor()
			.expectRequest("request-context.xml")
			.returnResponse("response2.xml");
		
		int result = calc.plus(1, 4);
		assertEquals(5, result);
		
		mockControl.verify();
	}
	
	@Test(expected=SoapFaultClientException.class)
	public void testException()
	{
		mockControl.validateSchema("xsd/calc.xsd").expectRequest("request-ignore.xml").returnResponse("fault.xml");
		calc.plus(2, 3);
	}
}
