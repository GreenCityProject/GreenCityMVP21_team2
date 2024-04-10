package greencity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration for AOP-related parts of the application.
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy
public class AOPConfig {
}
