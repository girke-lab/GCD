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

/*
 * @(#)$Id: SAXUnmarshallerHandler.java,v 1.5 2003/05/14 18:36:18 lschwenk Exp $
 */
package GO.go_jaxb.impl.runtime;

import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;

import org.xml.sax.SAXException;

/**
 * Unified event handler that processes
 * both the SAX events and error events.
 */
public interface SAXUnmarshallerHandler extends UnmarshallerHandler {
    
    /**
     * Reports an error to the user, and asks if s/he wants
     * to recover. If the canRecover flag is false, regardless
     * of the client instruction, an exception will be thrown.
     * 
     * Only if the flag is true and the user wants to recover from an error,
     * the method returns normally.
     * 
     * The thrown exception will be catched by the unmarshaller.
     */
    void handleEvent( ValidationEvent event, boolean canRecover ) throws SAXException;
}
