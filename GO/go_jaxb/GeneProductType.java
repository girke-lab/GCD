//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.2-b15-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 01:01:22 PST 
//


package GO.go_jaxb;


/**
 * Java content class for anonymous complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/home/khoran/javaPrograms/svnCO/GO/go4.xsd line 170)
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}dbxref"/>
 *       &lt;/sequence>
 *       &lt;attribute name="parseType" type="{http://www.w3.org/2001/XMLSchema}string" fixed="Resource" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface GeneProductType {


    /**
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     *     {@link java.lang.String}
     */
    java.lang.String getParseType();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     *     {@link java.lang.String}
     */
    void setParseType(java.lang.String value);

    /**
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getName();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setName(java.lang.String value);

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
