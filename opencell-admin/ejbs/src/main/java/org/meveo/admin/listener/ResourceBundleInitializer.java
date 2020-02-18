package org.meveo.admin.listener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;

@Stateless
public class ResourceBundleInitializer {

    private static final String DEFAULT_PROVIDER_RESOURCES_DIR = "/media/";

    @Inject
    private Logger log;

    private ParamBean paramBean = ParamBean.getInstance();

    private String[] i18n = { "messages_en.properties", "messages_fr.properties" };

    public void init() {

        try {
            log.info("Start ResourceBundleInitializer init");
            Path target = getTargetDir();
            if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectory(target);
            }

            for (String fileName : i18n) {
                Path targetFile = Paths.get(target.toString(), fileName);
                InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
                Files.copy(in, targetFile);
            }

            log.info("End ResourceBundleInitializer init");

        } catch (FileAlreadyExistsException e) {

        } catch (IOException e) {
            log.info("Error ResourceBundleInitializer init", e);
        }

    }

    public Path getTargetDir() {

        String rootDir = paramBean.getProperties().getProperty("providers.rootDir");
        String providerDir = paramBean.getProperties().getProperty("provider.rootDir");
        String providerRsDir = paramBean.getProperty("provider.resourcesDir", DEFAULT_PROVIDER_RESOURCES_DIR);
        return Paths.get(rootDir, providerDir, providerRsDir, "i18n");

    }

}