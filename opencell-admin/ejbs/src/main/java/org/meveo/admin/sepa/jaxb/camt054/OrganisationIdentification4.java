//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.09.13 at 04:10:01 PM WET 
//


package org.meveo.admin.sepa.jaxb.camt054;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrganisationIdentification4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganisationIdentification4"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BICOrBEI" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.02}AnyBICIdentifier" minOccurs="0"/&gt;
 *         &lt;element name="Othr" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.02}GenericOrganisationIdentification1" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationIdentification4", propOrder = {
    "bicOrBEI",
    "othr"
})
public class OrganisationIdentification4 {

    @XmlElement(name = "BICOrBEI")
    protected String bicOrBEI;
    @XmlElement(name = "Othr")
    protected List<GenericOrganisationIdentification1&gt; othr;

    /**
     * Gets the value of the bicOrBEI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBICOrBEI() {
        return bicOrBEI;
    }

    /**
     * Sets the value of the bicOrBEI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBICOrBEI(String value) {
        this.bicOrBEI = value;
    }

    /**
     * Gets the value of the othr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE&gt;set</CODE&gt; method for the othr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOthr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenericOrganisationIdentification1 }
     * 
     * 
     */
    public List<GenericOrganisationIdentification1&gt; getOthr() {
        if (othr == null) {
            othr = new ArrayList<GenericOrganisationIdentification1&gt;();
        }
        return this.othr;
    }

}
