package com.sling.webflux.webflux.sse;

import com.sling.webflux.webflux.WebfluxApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * User: sunling
 * Date: 2023/10/23 14:05
 * Description:
 **/
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebfluxApplication.class);
	}

}
