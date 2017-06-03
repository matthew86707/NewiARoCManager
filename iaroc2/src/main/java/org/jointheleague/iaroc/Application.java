package org.jointheleague.iaroc;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.jointheleague.iaroc.config.JerseyConfig;
import org.jointheleague.iaroc.db.DBUtils;
import org.jointheleague.iaroc.model.Announcements;
import org.jointheleague.iaroc.model.EntityManager;
import org.jointheleague.iaroc.rest.RestResource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.sql.Connection;

/**
 * Date: 22/12/13
 * Time: 18:03
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {RestResource.class})
public class Application {
	
	public static String password;

    public static void main(String[] args) throws Exception {

        Connection con = DBUtils.createConnection();
        if(args.length > 0 && args[0].equals("clear")){
            EntityManager.createTables(con);
        }else if(args.length > 0 && args[0].equals("debug")){
            EntityManager.addDummyData(con);
        }
        
        password = args[1];

        Announcements.init();

        new SpringApplicationBuilder(Application.class)
                .showBanner(false)
                .run(args);
    }

    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/rest/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        return registration;
    }
}
