package org.meveo.commons.compilation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;

import org.apache.commons.lang3.StringUtils;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileFilter;
import org.jboss.vfs.protocol.AbstractURLConnection;
import org.primefaces.expression.impl.FindComponentExpressionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The java source compiler
 * 
 * @author Axione
 *
 */
@Stateless
@Named
public class JavaSourceCompiler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSourceCompiler.class);
    private static final String JAR_EXT = ".jar";
    
    private CompilerClassLoader classLoader;
    private JavaCompiler javaCompiler;
    
    /**
     * The compiler class loader
     */
    private static final class CompilerClassLoader extends URLClassLoader {
        private static final String JBOSS_SERVER_BASE_DIR_PROPERTY = "jboss.server.base.dir";
        private static final String LIB_EXT_DIR = "/lib/ext";
        private ConcurrentMap<String, List<JavaFileObject>> packageFileObjects = new ConcurrentHashMap<>();
        private ConcurrentMap<String, InMemoryJavaFileObject> inMemoryFiles = new ConcurrentHashMap<>();
        private Map<String, Class<?>> compiledClasses = new ConcurrentHashMap<>();

        /**
         * Jar file filter
         * 
         * @return A file filter for jar file
         */
        private static FileFilter jarFileFilter() {
            return new FileFilter() {
                
                @Override
                public boolean accept(File file) {
                    return file.getPath().endsWith(JAR_EXT);
                }
            };
        }
        
        /**
         * File to URL
         * 
         * @param file - The file
         * @return URL of file
         */
        private static URL fileURL(File file) {
            try {
                return file.toURI().toURL();
            }
            catch (MalformedURLException e) {
                LOGGER.warn("Bad file url [{}] -> ({})", file, e);
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Get all jar URL from lib/ext directory
         * 
         * @return  All jar URL from lib ext dir
         */
        private static URL[] urls() {
            String libExtPath = System.getProperty(JBOSS_SERVER_BASE_DIR_PROPERTY) + LIB_EXT_DIR;
            File libExtDir = new File(libExtPath);
            
            if (!libExtDir.isDirectory()) {
                throw new IllegalArgumentException("Lib Ext Dir [" + libExtPath + " not found");
            }
            
            return Arrays.stream(libExtDir.listFiles(jarFileFilter()))
                         .map(CompilerClassLoader::fileURL)
                         .toArray(URL[]::new);
        }
        
        /**
         * Construct it
         * 
         * @param parentClassLoader - The parent class loader
         */
        public CompilerClassLoader(ClassLoader parentClassLoader) {
            super(urls(), parentClassLoader);
            LOGGER.debug("Compiler class loader URLs {}", Arrays.asList(getURLs()));
        }
        
        /**
         * Open the URL connection
         */
        private URLConnection openURLConnection(final URL url) {
            try {
                return url.openConnection();
            }
            catch (IOException e) {
                LOGGER.warn("Can't open URL [{}] -> {}", url, e);
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Jar entry is a class ?
         * 
         * @return a jar class fileter
         */
        private boolean isClass(JarEntry jarEntry) {
            return jarEntry.getName().endsWith(Kind.CLASS.extension);
        }
        
        /**
         * Scan a jar URL connection
         * 
         * @param urlConnection - The jar URL connection
         * @return Stream of java file in jar
         */
        private Stream<JavaFileObject> scanJarURL(JarURLConnection urlConnection) {
            try {
                JarFile jarFile = urlConnection.getJarFile();
                
                return jarFile.stream()
                              .filter(this::isClass)
                              .map(jarEntry -> new JarJavaFileObject(jarFile, jarEntry));
            }
            catch (IOException e) {
                LOGGER.warn("Can't scan jar URL [{}] -> ({}", urlConnection, e);
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Filter on virtual file is a class
         */
        private VirtualFileFilter classFilter() {
            return new VirtualFileFilter() {
                
                @Override
                public boolean accepts(VirtualFile file) {
                    return file.getName().endsWith(Kind.CLASS.extension);
                }
            };
        }

        /**
         * Scan the virtual URL connection
         * 
         * @param urlConenction - The virtual URL connection
         * @return Stream of java file object contains in jar of url
         */
        private Stream<JavaFileObject> scanVirtualURL(AbstractURLConnection urlConnection) {
            try {
                VirtualFile vf = (VirtualFile) urlConnection.getContent();
                return vf.getChildren(classFilter())
                         .stream()
                         .map(child -> new VirtualJavaFileObject(vf, child));
            }
            catch (IOException e) {
                LOGGER.warn("Can't scan virtual URL [{}] -> ({})", urlConnection, e);
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Scan a URL
         * 
         * @param url - The url
         * @return List of java file in this URL
         */
        private Stream<JavaFileObject> scanURL(final URL url) {
            URLConnection urlConnection = openURLConnection(url);
            
            if (urlConnection instanceof JarURLConnection) {
                return scanJarURL((JarURLConnection) urlConnection);
            }
            
            if (urlConnection instanceof AbstractURLConnection) {
                return scanVirtualURL((AbstractURLConnection) urlConnection);
            }
            
            LOGGER.warn("Unkonw URL connection type []", urlConnection.getClass());
            return Stream.of();
        }
        
        /**
         * Scan a package
         * 
         * @param packageName - Name of package
         * @return List of Java file for this package
         */
        private List<JavaFileObject> scanPackage(final String packageName) {
            try {
                Enumeration<URL> packageURLs = getResources(packageName.replace('.', '/'));
                
                if (!packageURLs.hasMoreElements()) {
                    return Collections.emptyList();
                }
                
                List<JavaFileObject> scan = Collections.list(packageURLs)
                                                       .stream()
                                                       .flatMap(this::scanURL)
                                                       .collect(Collectors.toList());
                
                LOGGER.debug("Scan package [{}] -> ({})", packageName, scan.size());
                return scan;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * List a package
         * 
         * @param packageName - The package name
         */
        public List<JavaFileObject> list(String packageName) {
            return packageFileObjects.computeIfAbsent(packageName, this::scanPackage);
        }
        
        /**
         * Register a class name for compilation
         * 
         * @param className - The class name
         * @return The new in memory java file
         */
        public JavaFileObject registerClass(final String className) {
            InMemoryJavaFileObject javaFileObject = new InMemoryJavaFileObject(className);

            inMemoryFiles.put(className, javaFileObject);
            LOGGER.debug("Register class [{}]", className);

            return javaFileObject;
        }
        
        @Override
        public Class<?> findClass(String className) throws ClassNotFoundException {
            Class<?> zClass = compiledClasses.get(className);
            
            if (zClass != null) {
                return zClass;
            }
            
            InMemoryJavaFileObject javaFileObject = inMemoryFiles.get(className);
            
            if (javaFileObject != null) {
                byte[] byteCode = javaFileObject.byteCode();
                zClass = defineClass(className, byteCode, 0, byteCode.length);
                compiledClasses.put(className, zClass);
                return zClass;
            }
            
            return super.findClass(className);
        }
    }
    
    /**
     * The java source
     */
    private static final class JavaSource {
        private final String className;
        private final String source;
        
        /**
         * Construt it
         * 
         * @param classname - The class name
         * @param source - The source
         */
        public JavaSource(final String className, final String source) {
            this.className = className;
            this.source = source;
        }

        public String getClassName() {
            return className;
        }

        public String getSource() {
            return source;
        }
    }
    
    /**
     * The parser of java source
     */
    public static final class JavaSourceParser {
        /**
         * Parse a source
         * 
         * @param source - The source
         */
        public static JavaSource parse(final String source) {
            if (StringUtils.isBlank(source)) {
                throw new IllegalArgumentException("Source is null or empty !!!");
            }
            
            Matcher m = Pattern.compile("package (.*);").matcher(source);
            
            if (!m.find()) {
                throw new IllegalArgumentException("Package not found in source");
            }
            
            String packageName = m.group(1);
            
            m = Pattern.compile(".*class (\\w*)").matcher(source);
            
            if (!m.find()) {
                throw new IllegalArgumentException("Class not found in source");
            }
            
            String className = packageName + '.' + m.group(1);
            
            return new JavaSource(className, source);
        }
        
    }

    /**
     * The in memory source java file object 
     */
    private static final class SourceJavaFileObject extends SimpleJavaFileObject {
        private String className;
        private CharSequence source;
        
        /**
         * Construct it from java source
         * 
         * @param javaSource - The java source
         */
        public SourceJavaFileObject(final JavaSource javaSource) {
            super(URI.create("mem:///" + javaSource.getClassName().replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.className = javaSource.getClassName();
            this.source = javaSource.getSource();
        }
        
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
        
        public String getClassName() {
            return className;
        }
    }
    
    /**
     * A jar java file object
     */
    private static final class JarJavaFileObject extends SimpleJavaFileObject {
        private String className;
        private JarFile jarFile;
        private JarEntry jarEntry;
        
        /**
         * Create it
         * 
         * @param jarFile - The jar file
         * @param jarEntry - The jar entry
         */
        public JarJavaFileObject(final JarFile jarFile, final JarEntry jarEntry) {
            super(URI.create(jarFile.getName() + '!' + jarEntry.getName()), Kind.CLASS);
            this.jarFile = jarFile;
            this.jarEntry = jarEntry;
            this.className = jarEntry.getName().replace('/', '.').replaceAll(Kind.CLASS.extension + '$', "");
        }
        
        @Override
        public InputStream openInputStream() throws IOException {
            return jarFile.getInputStream(jarEntry);
        }

        public String getClassName() {
            return className;
        }
    }
    
    /**
     * Virtual file java file object
     */
    private static final class VirtualJavaFileObject extends SimpleJavaFileObject {
        private VirtualFile vf;
        private String className;
        
        /**
         * Construct it
         * 
         * @param parent - The parent virtual file
         * @param vf - The virtual file
         */
        public VirtualJavaFileObject(final VirtualFile parent, final VirtualFile vf) {
            super(URI.create(parent.getPathName() + '!' + vf.getName()), Kind.CLASS);
            this.vf = vf;
            className = vf.getName().replace('/', '.').replaceAll(Kind.CLASS.extension + '$', "");
        }
        
        @Override
        public InputStream openInputStream() throws IOException {
            return vf.openStream();
        }
        
        public String getClassName() {
            return className;
        }
        
    }
    
    /**
     * The in memory byte code java file object
     */
    private static final class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private String className;
        private ByteArrayOutputStream byteCodeStream;
        
        /**
         * Construct it
         * 
         * @param className - The class name
         */
        public InMemoryJavaFileObject(final String className) {
            super(URI.create("mem:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
            this.className = className;
        }
        
        @Override
        public OutputStream openOutputStream() {
            byteCodeStream = new ByteArrayOutputStream();
            return byteCodeStream;
        }

        public String getClassName() {
            return className;
        }
        
        public byte[] byteCode() {
            return byteCodeStream.toByteArray();
        }
        
    }
    
    /**
     * The compiler java file manager
     */
    private static final class CompilerJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private CompilerClassLoader classLoader;
        
        /**
         * Construct it
         * 
         * @param fileManager - The forward file manager
         * @param classLoader - The compiler class loader
         */
        public CompilerJavaFileManager(JavaFileManager fileManager, CompilerClassLoader classLoader) {
            super(fileManager);
            this.classLoader = classLoader;
        }
        
        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
            Iterable<JavaFileObject> superList = super.list(location, packageName, kinds, recurse);
            List<JavaFileObject> list = new ArrayList<>();
            superList.forEach(list::add);
            list.addAll(this.classLoader.list(packageName));
            LOGGER.debug("List package [{}] , [{}] class found", packageName, list.size());
            return list;
        }
        
        @Override
        public String inferBinaryName(Location location, JavaFileObject javaFileObject) {
           
            if (javaFileObject instanceof SourceJavaFileObject) {
                return ((SourceJavaFileObject) javaFileObject).getClassName();
            }
            
            if (javaFileObject instanceof JarJavaFileObject) {
                return ((JarJavaFileObject) javaFileObject).getClassName();
            }
            
            if (javaFileObject instanceof VirtualJavaFileObject) {
                return ((VirtualJavaFileObject) javaFileObject).getClassName();
            }
            
            return super.inferBinaryName(location, javaFileObject);
        }
        
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
            LOGGER.debug("Get java file for output [{}]", className);
            return this.classLoader.registerClass(className);
        }
               
    }
    
    /**
     * Get the standard compiler
     * 
     * @return The standard compiler
     */
    private JavaCompiler javaCompiler() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        
        if (javaCompiler == null) {
            throw new RuntimeException("Can't find a java compiler !!!");
        }
        
        return javaCompiler;
    }

    /**
     * Create the java file manager
     * 
     * @return A new java file manager
     */
    private CompilerJavaFileManager javaFileManager() {
        JavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(new DiagnosticCollector<>(), null, null);
        
        if (standardJavaFileManager == null) {
            LOGGER.warn("Can't find the standard java file manager");
            throw new RuntimeException("Can't get java file manager !!!");
        }
        
        LOGGER.debug("Standard java file manager found !!!");
        
        return new CompilerJavaFileManager(standardJavaFileManager, classLoader);
    }
    
    
    /**
     * Initialize the compiler
     */
    @PostConstruct
    public void initialize() {
        classLoader = new CompilerClassLoader(this.getClass().getClassLoader());
        javaCompiler = javaCompiler();
    }
    
    /**
     * Get the class loader
     * 
     * @return classLoader - The class loader
     */
    public CompilerClassLoader classLoader() {
        return classLoader;
    }

    /**
     * Compile a source
     * 
     * @param source - The source
     * @return 
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> compile(String source) {
        JavaSource javaSource = null;
        
        LOGGER.debug("Start compile of source {}", source);
        
        try  {
            javaSource = JavaSourceParser.parse(source);
            LOGGER.debug("Source parsed -> [{}]", javaSource.getClassName());
        }
        catch (Exception e) {
            LOGGER.warn("Invalid code source !!!!");
            throw new CompilationException(e);
        }
        
        JavaFileObject compilationUnit = new SourceJavaFileObject(javaSource);
        DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<>();
        CompilerJavaFileManager javaFileManager = javaFileManager();
        
        boolean compiled = false;
        
        try {
            compiled = javaCompiler.getTask(null, javaFileManager, diag, null, null, Arrays.asList(compilationUnit))
                                   .call();
            LOGGER.debug("Compilation [{}] status ({})", javaSource.getClassName(), compiled);
        }
        catch (Exception e) {
            LOGGER.warn("Error during compilation -> {}", e);
            throw new CompilationException(e);
        }
        
        if (!compiled) {
            CompilationException compilationException = new CompilationException(diag);
            LOGGER.warn("Compilation error -> {}", compilationException.getMessage());
            throw compilationException;
        }
        
        LOGGER.debug("Class loader in memory files !!! {}", classLoader.inMemoryFiles);
        
        
        try {
          Class<T> zClass =  (Class<T>) classLoader.findClass(javaSource.getClassName());
          LOGGER.debug("Class [{}] compiled with success", zClass.getName());
          return zClass;
        }
        catch (ClassNotFoundException e) {
            LOGGER.warn("Can't find class [{}] in class loader !!!", javaSource.getClassName());
            throw new CompilationException(e);
        }
        catch (ClassCastException e) {
            LOGGER.warn("Can't cast class [{}] !!!", javaSource.getClassName());
            throw new CompilationException(e);
        }
    
    }

}
