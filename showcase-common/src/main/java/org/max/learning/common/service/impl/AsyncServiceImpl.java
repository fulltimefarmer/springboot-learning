package org.max.learning.common.service.impl;

import org.max.learning.common.service.AsyncService;
import org.max.learning.common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncServiceImpl implements AsyncService {

	@Autowired
	TestService testService;
	
	@Async
	@Override
	public void asyncRun() {
		testService.run();
	}

}
