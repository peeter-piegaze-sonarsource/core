package org.meveo.commons.compilation;

import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;


/**
 * The compilation exceptions
 * 
 * @author Axione
 *
 */
public class CompilationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private DiagnosticCollector<JavaFileObject> diagnostic;
    
    /**
     * Construct it
     * @param diags - The diags
     */
    public CompilationException(DiagnosticCollector<JavaFileObject> diags) {
        super(diags.getDiagnostics().stream().map(Diagnostic::toString).collect(Collectors.joining(System.lineSeparator())));
        this.diagnostic = diags;
    }
    
    /**
     * Construct it
     * 
     * @param cause - The cause
     */
    public CompilationException(Throwable cause) {
        super("Compilation error -> " + cause, cause);
    }
    
    public DiagnosticCollector<JavaFileObject> getDiagnostic() {
      return diagnostic;   
    }
}
