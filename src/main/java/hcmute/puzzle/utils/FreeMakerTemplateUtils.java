package hcmute.puzzle.utils;

import freemarker.cache.*;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.RTFOutputFormat;
import freemarker.core.TemplateConfiguration;
import freemarker.core.XMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateExceptionHandler;
import hcmute.puzzle.configuration.FreemarkerConfiguration;

import java.io.File;
import java.io.IOException;

public class FreeMakerTemplateUtils {

    public static Configuration getFreeMakerConfiguration() throws IOException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setDefaultEncoding("UTF-8");
        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_27);
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

    cfg.setTemplateLoader(new FileTemplateLoader(new File("/templates")));
        return cfg;
    }

}
