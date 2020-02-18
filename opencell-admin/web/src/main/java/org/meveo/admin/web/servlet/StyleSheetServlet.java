package org.meveo.admin.web.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copy the resource from the war (without overriding existing files) to some folder in provider "rootDir" (for example, ./media/stylesheet/ and ./media/img/). <br>
 * And of course, use these files instead of those from the WAR.
 */

@WebServlet(name = "styleSheetServlet", urlPatterns = { "/stylesheet/*", "/img/*" }, loadOnStartup = 1)
public class StyleSheetServlet extends HttpServlet 
{

    private static final long serialVersionUID = -8639313078502817871L;
    private final Logger log = LoggerFactory.getLogger(StyleSheetServlet.class);
    private static final String DEFAULT_PROVIDER_RESOURCES_DIR = "/media";
    private ParamBean paramBean = ParamBean.getInstance();
    private Path styleSheetSource;
    private Path styleSheetTarget;
    private Path imgSource;
    private Path imgTarget;

    @Override
    public void init() throws ServletException 
    {
        try {

            log.info("Start StyleSheetServlet init");
            String rootDir = paramBean.getProperties().getProperty("providers.rootDir");
            String providerDir = paramBean.getProperties().getProperty("provider.rootDir");
            String providerRsDir = paramBean.getProperty("provider.resourcesDir", DEFAULT_PROVIDER_RESOURCES_DIR);
            styleSheetSource = Paths.get(getServletContext().getRealPath(File.separator + "stylesheet"));
            styleSheetTarget = Paths.get(rootDir, providerDir, providerRsDir, "stylesheet");

            log.debug("StyleSheetServlet copy styleSheet directory");
            copyDirectory(styleSheetSource, styleSheetTarget);
            imgSource = Paths.get(getServletContext().getRealPath(File.separator + "img"));
            imgTarget = Paths.get(rootDir, providerDir, providerRsDir, "img");

            log.debug("StyleSheetServlet copy img directory");
            copyDirectory(imgSource, imgTarget);
            log.info("End StyleSheetServlet init");

        } catch (IOException e) {
            log.error("Error StyleSheetServlet init", e);
            throw new ServletException(e);

        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        String[] path = req.getRequestURI().split("/");
        String fileName = path[path.length - 1];
        if (req.getServletPath().equals("/stylesheet")) {
            Path fileTarget = Paths.get(styleSheetTarget.toString(), fileName);
            if (!Files.exists(fileTarget, LinkOption.NOFOLLOW_LINKS)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            buildCSSResponse(fileTarget, resp);
        } else if (req.getServletPath().equals("/img")) {
            Path fileTarget = Paths.get(imgTarget.toString(), fileName);
            if (!Files.exists(fileTarget, LinkOption.NOFOLLOW_LINKS)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            buildImgResponse(fileName, fileTarget, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
        }
    }

    private void buildCSSResponse(Path fileTarget, HttpServletResponse resp) throws IOException 
    {
        resp.setContentType("text/css");
        resp.setContentLength((int) fileTarget.toFile().length());
        FileUtils.copyFile(fileTarget.toFile(), resp.getOutputStream());
    }

    private void buildImgResponse(String fileName, Path fileTarget, HttpServletResponse resp) throws IOException 
    {
        String mimeType = Files.probeContentType(fileTarget);
        log.debug("Files.probeContentType mimeType found={}", mimeType);
        
        if (mimeType == null) {
            mimeType = "image/" + getImageFormat(fileTarget.toFile());
            log.debug("getImageFormat mimeType found={}", mimeType);
        }

        // modifies response
        resp.setContentType(mimeType);
        resp.setContentLength((int) fileTarget.toFile().length());
        BufferedImage img = ImageIO.read(fileTarget.toFile());
        ImageIO.write(img, fileName.substring(fileName.indexOf('.') + 1), resp.getOutputStream());
    }

    private String getImageFormat(File file) throws IOException 
    {
        // create an image input stream from the specified file
        ImageInputStream iis = ImageIO.createImageInputStream(file);

        // get all currently registered readers that recognize the image format
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);

        if (!iter.hasNext()) {
            log.error("No reader found for image");
        }

        // get the first reader
        ImageReader reader = iter.next();
        String formatName = reader.getFormatName();

        // close stream
        iis.close();
        return formatName;
    }

    private void copyDirectory(Path source, Path target) throws IOException 
    {
        try {
            if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectory(target);
            }

            for (Path fileSource : getFiles(source)) {
                Path fileTarget = Paths.get(target.toString(), fileSource.getFileName().toString());
                Files.copy(fileSource, fileTarget);
            }
        } catch (FileAlreadyExistsException e) {

        }
    }

    private List<Path> getFiles(Path inputDir) throws IOException 
    {
        try (Stream<Path> filesStream = Files.list(inputDir)) {
            return filesStream.map(Path::toAbsolutePath).collect(Collectors.toList());
        }
    }

}