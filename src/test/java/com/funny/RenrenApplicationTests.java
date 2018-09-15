package com.funny;

import com.funny.utils.AESUtils;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(JUnit4.class)
//@SpringBootTest
public class RenrenApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println(AESUtils.encrypt("466243_TVEQB1WNSH_20181201","01234567890123456789012345678912"));

	}

}
