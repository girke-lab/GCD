//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.2-b15-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 01:01:22 PST 
//


package GO.go_jaxb;


/**
 * Java content class for anonymous complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/home/khoran/javaPrograms/svnCO/GO/go4.xsd line 186)
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}database_symbol"/>
 *         &lt;element ref="{}reference"/>
 *       &lt;/sequence>
 *       &lt;attribute name="parseType" type="{http://www.w3.org/2001/XMLSchema}string" fixed="Resource" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface DbxrefType {


    /**
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getDatabaseSymbol();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setDatabaseSymbol(java.lang.String value);

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
     *     {@link GO.go_jaxb.Reference}
     *     {@link GO.go_jaxb.ReferenceType}
     */
    GO.go_jaxb.ReferenceType getReference();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link GO.go_jaxb.Reference}
     *     {@link GO.go_jaxb.ReferenceType}
     */
    void setReference(GO.go_jaxb.ReferenceType value);

}
