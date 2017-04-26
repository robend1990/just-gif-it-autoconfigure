package com.justgifit;

import java.io.File;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.justgifit.services.ConverterService;
import com.justgifit.services.GifEncoderService;
import com.justgifit.services.VideoDecoderService;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

@Configuration
@ConditionalOnMissingBean({FFmpegFrameGrabber.class, AnimatedGifEncoder.class})
public class JustGifItAutoConfiguration {

	@Value("${multipart.location}/gif/")
    private String gifLocation;
	
	 @Bean
	 @ConditionalOnProperty(prefix = "com.justgifit", name = "create-result-dir")
	 public Boolean createResultDir() {
		 File gifFolder = new File(gifLocation);
		 if (!gifFolder.exists()) {
			 gifFolder.mkdir();
		 }
		 return true;
	 }
	
	@Bean
	@ConditionalOnMissingBean(VideoDecoderService.class)
	public VideoDecoderService videoDecoderService() {
		return new VideoDecoderService();
	}
	
	@Bean
	@ConditionalOnMissingBean(GifEncoderService.class)
	public GifEncoderService gifEncoderService() {
		return new GifEncoderService();
	}
	
	@Bean
	@ConditionalOnMissingBean(ConverterService.class)
	public ConverterService converterService() {
		return new ConverterService();
	}
	
	@Configuration
	@ConditionalOnWebApplication
	public static class WebConfiguration {
		
		@Value("${multipart.location}/gif/")
	    private String gifLocation;
		
		@Bean
	    public WebMvcConfigurer webMvcConfigurer() {
	        return new WebMvcConfigurerAdapter() {
	            @Override
	            public void addResourceHandlers(ResourceHandlerRegistry registry) {
	                registry.addResourceHandler("/gif/**")
	                        .addResourceLocations("file:" + gifLocation);
	                super.addResourceHandlers(registry);
	            }
	        };
	    }
	    
	    
	    /**
	     * Deregister filter which allows to use other http method that post and get 
	     * @param filter
	     * @return
	     */
	    @Bean
	    @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
	    public FilterRegistrationBean deregisterHiddenHttpMethodFilter(HiddenHttpMethodFilter filter) {
	    	FilterRegistrationBean bean = new FilterRegistrationBean(filter);
	    	bean.setEnabled(false);
	    	return bean;
	    }
	    
	    /**
	     * We are not using PUT method. lets disable put filter
	     * @param filter
	     * @return
	     */
	    @Bean
	    @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
	    public FilterRegistrationBean deregisterHttpPutFormContentFilter(HttpPutFormContentFilter filter) {
	    	FilterRegistrationBean bean = new FilterRegistrationBean(filter);
	    	bean.setEnabled(false);
	    	return bean;
	    }
	    
	    /**
	     * We are not using any request and session scoped beans. Lets disable filter
	     * @param filter
	     * @return
	     */
	    @Bean
	    @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
	    public FilterRegistrationBean deregisterRequestContextFilter(RequestContextFilter filter) {
	    	FilterRegistrationBean bean = new FilterRegistrationBean(filter);
	    	bean.setEnabled(false);
	    	return bean;
	    }
		
	}
}
