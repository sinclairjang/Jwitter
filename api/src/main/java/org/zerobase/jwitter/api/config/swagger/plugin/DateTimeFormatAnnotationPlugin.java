package org.zerobase.jwitter.api.config.swagger.plugin;

import org.springframework.core.annotation.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.builders.StringElementFacetBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.util.Optional;

import static springfox.bean.validators.plugins.Validators.annotationFromBean;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class DateTimeFormatAnnotationPlugin implements ModelPropertyBuilderPlugin {
    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<DateTimeFormat> dateTimeFormat = annotationFromBean(context,
                DateTimeFormat.class);
        if (dateTimeFormat.isPresent()) {
            context.getSpecificationBuilder().facetBuilder(StringElementFacetBuilder.class)
                    .pattern(dateTimeFormat.get().pattern());
        }
    }
}
