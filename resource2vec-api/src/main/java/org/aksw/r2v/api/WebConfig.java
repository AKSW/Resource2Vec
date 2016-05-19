package org.aksw.r2v.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

		// paths where '.' should rewrite to 'index.html'
		String[] paths = { "" };
		for (String path : paths)
			rewrite(path, path + "/index.html", registry);
				
		// current API version
		redirect("/api", "/api/v0.1", registry);
		
		// redirect to view Slate documentation correctly
		redirect("/api/v0.1", "/api/v0.1/index.html", registry);
		
	}

	private void rewrite(String origin, String destination,
			ViewControllerRegistry registry) {
		registry.addViewController(origin).setViewName("forward:" + destination);
	}
	
	private void redirect(String origin, String destination,
			ViewControllerRegistry registry) {
		registry.addViewController(origin).setViewName("redirect:" + destination);
		registry.addViewController(origin + "/").setViewName("redirect:" + destination);
	}

}