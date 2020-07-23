package org.max.learning.common.service.impl;

import org.max.learning.common.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

	int count = 1;
	
	@Override
	public void run() {
		try {
	    	count++;
	    	if(count % 2 ==0) {
	    		Thread.sleep(9_000L);
	    	}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
