//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.2-b15-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 01:01:22 PST 
//

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package GO.go_jaxb.impl.runtime;

import org.xml.sax.SAXException;

/**
 * For a generated class to be serializable, it has to
 * implement this interface.
 * 
 * @author Kohsuke Kawaguchi
 */
public interface XMLSerializable
{
    /**
     * Serializes child elements and texts into the specified target.
     */
    void serializeBody( XMLSerializer target ) throws SAXException;
    
    /**
     * Serializes attributes into the specified target.
     */
    void serializeAttributes( XMLSerializer target ) throws SAXException;
    
    /**
     * Declares all the namespace URIs this object is using at
     * its top-level scope into the specified target.
     */
    void serializeURIs( XMLSerializer target ) throws SAXException;

}
