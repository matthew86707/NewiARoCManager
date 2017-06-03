package org.jointheleague.iaroc.test.config;

import org.jointheleague.iaroc.rest.RestResource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Date: 31/01/2014
 * Time: 00:41
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@Configuration
@ComponentScan(basePackageClasses = {RestResource.class})
public class TestConfig {
}
