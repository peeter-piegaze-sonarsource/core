
@XmlJavaTypeAdapter(value = DateTimeAdapter.class, type = Date.class)
package org.meveo.api.dto.catalog;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meveo.api.jaxb.DateTimeAdapter;