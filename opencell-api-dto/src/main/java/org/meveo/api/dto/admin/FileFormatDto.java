package org.meveo.api.dto.admin;

import org.meveo.api.dto.AuditableEntityDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class FileFormatDto.
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@XmlRootElement(name = "FileFormat")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileFormatDto extends AuditableEntityDto {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -2539099102487957375L;

    /**
     * The FileFormat code.
     */
    @XmlElement(required = true)
    private String code;

    /**
     * The FileFormat description.
     */
    private String description;

    /**
     * The file name pattern.
     */
    private String fileNamePattern;

    /**
     * The file type codes.
     */
    private List<String> fileTypes = new ArrayList<>();

    /**
     * The configuration template.
     */
    private String configurationTemplate;

    /**
     * The record name.
     */
    private String recordName;

    /**
     * The input directory.
     */
    private String inputDirectory;

    /**
     * The output directory.
     */
    @XmlElement(required = true)
    private String outputDirectory;

    /**
     * The reject directory.
     */
    private String rejectDirectory;

    /**
     * The archive directory.
     */
    private String archiveDirectory;

    /**
     * Gets the code
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the fileNamePattern
     *
     * @return the fileNamePattern
     */
    public String getFileNamePattern() {
        return fileNamePattern;
    }

    /**
     * Sets the fileNamePattern.
     *
     * @param fileNamePattern the new fileNamePattern
     */
    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    /**
     * Gets the fileTypes
     *
     * @return the fileTypes
     */
    public List<String> getFileTypes() {
        return fileTypes;
    }

    /**
     * Sets the fileTypes.
     *
     * @param fileTypes the new fileTypes
     */
    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * Gets the configurationTemplate
     *
     * @return the configurationTemplate
     */
    public String getConfigurationTemplate() {
        return configurationTemplate;
    }

    /**
     * Sets the configurationTemplate.
     *
     * @param configurationTemplate the new configurationTemplate
     */
    public void setConfigurationTemplate(String configurationTemplate) {
        this.configurationTemplate = configurationTemplate;
    }

    /**
     * Gets the recordName
     *
     * @return the recordName
     */
    public String getRecordName() {
        return recordName;
    }

    /**
     * Sets the recordName.
     *
     * @param recordName the new recordName
     */
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    /**
     * Gets the inputDirectory
     *
     * @return the inputDirectory
     */
    public String getInputDirectory() {
        return inputDirectory;
    }

    /**
     * Sets the inputDirectory.
     *
     * @param inputDirectory the new inputDirectory
     */
    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    /**
     * Gets the outputDirectory
     *
     * @return the outputDirectory
     */
    public String getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Sets the outputDirectory.
     *
     * @param outputDirectory the new outputDirectory
     */
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Gets the rejectDirectory
     *
     * @return the rejectDirectory
     */
    public String getRejectDirectory() {
        return rejectDirectory;
    }

    /**
     * Sets the rejectDirectory.
     *
     * @param rejectDirectory the new rejectDirectory
     */
    public void setRejectDirectory(String rejectDirectory) {
        this.rejectDirectory = rejectDirectory;
    }

    /**
     * Gets the archiveDirectory
     *
     * @return the archiveDirectory
     */
    public String getArchiveDirectory() {
        return archiveDirectory;
    }

    /**
     * Sets the archiveDirectory.
     *
     * @param archiveDirectory the new archiveDirectory
     */
    public void setArchiveDirectory(String archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
    }
}