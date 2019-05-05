package org.meveo.admin.util;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
public class ComponentResources implements Serializable {

	private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;

    private Locale locale = Locale.ENGLISH;
    
    @Inject
    @Client
    private Event<Locale> currentLocaleEventProducer;

    @Inject
    private LocaleSelector localeSelector;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Produces
    public ResourceBundle getResourceBundle() {
        String bundleName = "messages";
        if (FacesContext.getCurrentInstance() != null) {
            try {
                locale = localeSelector.getCurrentLocale();
                currentLocaleEventProducer.fire(locale);
                bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
            } catch (Exception e) {
            	log.error(e.getMessage(), e);
            }
        }

        return new ResourceBundle(java.util.ResourceBundle.getBundle(bundleName, locale));
    }

    @Produces
    @RequestScoped
    @Named("paramBean")
    @MeveoParamBean
    public ParamBean getParamBean() {
        return paramBeanFactory.getInstance();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}