//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.09.25 at 02:01:12 PM WET 
//


package org.meveo.admin.sepa.jaxb.pain001;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CashAccountType4Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CashAccountType4Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CASH"/>
 *     &lt;enumeration value="CHAR"/>
 *     &lt;enumeration value="COMM"/>
 *     &lt;enumeration value="TAXE"/>
 *     &lt;enumeration value="CISH"/>
 *     &lt;enumeration value="TRAS"/>
 *     &lt;enumeration value="SACC"/>
 *     &lt;enumeration value="CACC"/>
 *     &lt;enumeration value="SVGS"/>
 *     &lt;enumeration value="ONDP"/>
 *     &lt;enumeration value="MGLD"/>
 *     &lt;enumeration value="NREX"/>
 *     &lt;enumeration value="MOMA"/>
 *     &lt;enumeration value="LOAN"/>
 *     &lt;enumeration value="SLRY"/>
 *     &lt;enumeration value="ODFT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CashAccountType4Code")
@XmlEnum
public enum CashAccountType4Code {

    CASH,
    CHAR,
    COMM,
    TAXE,
    CISH,
    TRAS,
    SACC,
    CACC,
    SVGS,
    ONDP,
    MGLD,
    NREX,
    MOMA,
    LOAN,
    SLRY,
    ODFT;

    public String value() {
        return name();
    }

    public static CashAccountType4Code fromValue(String v) {
        return valueOf(v);
    }

}
