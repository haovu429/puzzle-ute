package hcmute.puzzle.configuration;

import freemarker.cache.*;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.RTFOutputFormat;
import freemarker.core.TemplateConfiguration;
import freemarker.core.XMLOutputFormat;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.IOException;

@Configuration
@EnableWebMvc
public class FreemarkerConfiguration {
    @Bean
    public FreeMarkerConfigurer freemarkerConfig() throws IOException {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        //freeMarkerConfigurer.setTemplateLoaderPath("/WEB-INF/views/");
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_27);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setDefaultEncoding("UTF-8");
        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_27);
        owb.setForceLegacyNonListCollections(false);
        owb.setDefaultDateType(TemplateDateModel.DATETIME);
        cfg.setObjectWrapper(owb.build());

        TemplateConfiguration tcHTML = new TemplateConfiguration();
        tcHTML.setOutputFormat(HTMLOutputFormat.INSTANCE);

        TemplateConfiguration tcXML = new TemplateConfiguration();
        tcXML.setOutputFormat(XMLOutputFormat.INSTANCE);

        TemplateConfiguration tcRTF = new TemplateConfiguration();
        tcRTF.setOutputFormat(RTFOutputFormat.INSTANCE);

        cfg.setTemplateConfigurations(
                new FirstMatchTemplateConfigurationFactory(
                        new ConditionalTemplateConfigurationFactory(
                                new FileExtensionMatcher("xml"),
                                tcXML),
                        new ConditionalTemplateConfigurationFactory(
                                new OrMatcher(
                                        new FileExtensionMatcher("html"),
                                        new FileExtensionMatcher("htm")),
                                tcHTML),
                        new ConditionalTemplateConfigurationFactory(
                                new FileExtensionMatcher("rtf"),
                                tcRTF)
                ).allowNoMatch(true)
        );

        cfg.setClassForTemplateLoading(this.getClass(), "/templates/");

        freeMarkerConfigurer.setConfiguration(cfg);
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/templates");
        return freeMarkerConfigurer;
    }
}
