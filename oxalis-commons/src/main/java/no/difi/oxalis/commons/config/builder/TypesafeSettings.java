/*
 * Copyright 2010-2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.oxalis.commons.config.builder;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import eu.peppol.lang.OxalisLoadingException;
import no.difi.oxalis.api.config.Settings;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author erlend
 * @since 4.0.0
 */
class TypesafeSettings<T> implements Settings<T> {

    private final Config config;

    private final Map<T, String> settings;

    @Inject
    public TypesafeSettings(Config config, Map<T, String> settings) {
        this.config = config;
        this.settings = settings;
    }

    @Override
    public String getString(T key) {
        if (config.hasPath(settings.get(key))) {
            return config.getString(settings.get(key));
        } else {
            try {
                Field field = key.getClass().getField(((Enum) key).name());

                if (field.getAnnotation(Nullable.class) != null)
                    return null;
                else if (field.getAnnotation(DefaultValue.class) != null)
                    return field.getAnnotation(DefaultValue.class).value();

                throw new OxalisLoadingException(String.format("Setting '%s' not found.", settings.get(key)));
            } catch (NoSuchFieldException e) {
                throw new OxalisLoadingException(e.getMessage(), e);
            }
        }
    }

    @Override
    public int getInt(T key) {
        return config.getInt(settings.get(key));
    }
}
