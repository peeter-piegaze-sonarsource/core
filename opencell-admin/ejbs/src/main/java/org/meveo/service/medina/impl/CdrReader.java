package org.meveo.service.medina.impl;

import java.io.IOException;

import org.meveo.model.rating.CDR;

/**
 * The Interface CdrReader.
 * @author h.znibar
 */
public interface CdrReader {

    /**
     * Initialize CDR Reader to read from originBatch.
     *
     * @param originBatch the origin batch
     */
    void init(String originBatch);
    
    /**
     * Get next record. A synchronized method to read from CDR source
     * 
     * @return CDR record
     * @throws IOException Failure to read the CDR source
     */
    CDR getNextRecord() throws IOException;
    
    /**
     * Close CDR record reader
     * 
     * @throws IOException IO exception
     */
    void close() throws IOException;
}
