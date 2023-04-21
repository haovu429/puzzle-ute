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

public class FreeMakerTemplateUtils {

    public static Configuration getFreeMakerConfiguration() {

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
        return cfg;
    }

}
