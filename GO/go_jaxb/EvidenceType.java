//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.2-b15-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 01:01:22 PST 
//


package GO.go_jaxb;


/**
 * Java content class for anonymous complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/home/khoran/javaPrograms/svnCO/GO/go4.xsd line 134)
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}dbxref" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="evidence_code" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="IEA"/>
 *             &lt;enumeration value="IMP"/>
 *             &lt;enumeration value="IGI"/>
 *             &lt;enumeration value="IPI"/>
 *             &lt;enumeration value="ISS"/>
 *             &lt;enumeration value="IDA"/>
 *             &lt;enumeration value="IEP"/>
 *             &lt;enumeration value="TAS"/>
 *             &lt;enumeration value="NAS"/>
 *             &lt;enumeration value="IC"/>
 *             &lt;enumeration value="ND"/>
 *             &lt;enumeration value="NR"/>
 *             &lt;enumeration value="NULL"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface EvidenceType {


    /**
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getEvidenceCode();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setEvidenceCode(java.lang.String value);

    /**
     * 
     * @return
     *     possible object is
     *     {@link GO.go_jaxb.DbxrefType}
     *     {@link GO.go_jaxb.Dbxref}
     */
    GO.go_jaxb.DbxrefType getDbxref();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link GO.go_jaxb.DbxrefType}
     *     {@link GO.go_jaxb.Dbxref}
     */
    void setDbxref(GO.go_jaxb.DbxrefType value);

}