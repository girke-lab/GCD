//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.2-b15-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 01:01:22 PST 
//


package GO.go_jaxb.impl;

public class GoImpl
    extends GO.go_jaxb.impl.GoTypeImpl
    implements GO.go_jaxb.Go, com.sun.xml.bind.RIElement, com.sun.xml.bind.JAXBObject, GO.go_jaxb.impl.runtime.UnmarshallableObject, GO.go_jaxb.impl.runtime.XMLSerializable, GO.go_jaxb.impl.runtime.ValidatableObject
{

    public final static java.lang.Class version = (GO.go_jaxb.impl.JAXBVersion.class);
    private static com.sun.msv.grammar.Grammar schemaFragment;

    private final static java.lang.Class PRIMARY_INTERFACE_CLASS() {
        return (GO.go_jaxb.Go.class);
    }

    public java.lang.String ____jaxb_ri____getNamespaceURI() {
        return "";
    }

    public java.lang.String ____jaxb_ri____getLocalName() {
        return "go";
    }

    public GO.go_jaxb.impl.runtime.UnmarshallingEventHandler createUnmarshaller(GO.go_jaxb.impl.runtime.UnmarshallingContext context) {
        return new GO.go_jaxb.impl.GoImpl.Unmarshaller(context);
    }

    public void serializeBody(GO.go_jaxb.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        context.startElement("", "go");
        super.serializeURIs(context);
        context.endNamespaceDecls();
        super.serializeAttributes(context);
        context.endAttributes();
        super.serializeBody(context);
        context.endElement();
    }

    public void serializeAttributes(GO.go_jaxb.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public void serializeURIs(GO.go_jaxb.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public java.lang.Class getPrimaryInterface() {
        return (GO.go_jaxb.Go.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        if (schemaFragment == null) {
            schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize((
 "\u00ac\u00ed\u0000\u0005sr\u0000\'com.sun.msv.grammar.trex.ElementPattern\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000"
+"\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xr\u0000\u001ecom.sun.msv."
+"grammar.ElementExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002Z\u0000\u001aignoreUndeclaredAttributesL\u0000"
+"\fcontentModelt\u0000 Lcom/sun/msv/grammar/Expression;xr\u0000\u001ecom.sun."
+"msv.grammar.Expression\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0003I\u0000\u000ecachedHashCodeL\u0000\u0013epsilon"
+"Reducibilityt\u0000\u0013Ljava/lang/Boolean;L\u0000\u000bexpandedExpq\u0000~\u0000\u0003xp\n\u00bb\u00c4\u0085p"
+"p\u0000sr\u0000\u001fcom.sun.msv.grammar.SequenceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.sun."
+"msv.grammar.BinaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0004exp1q\u0000~\u0000\u0003L\u0000\u0004exp2q\u0000~\u0000\u0003xq\u0000~"
+"\u0000\u0004\n\u00bb\u00c4zppsq\u0000~\u0000\u0007\t|\u00021ppsq\u0000~\u0000\u0007\u0007\u0002\rAppsq\u0000~\u0000\u0007\u00056\u0088\u0000ppsr\u0000\u001dcom.sun.msv."
+"grammar.ChoiceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\b\u0002\u00e9~\u0082ppsq\u0000~\u0000\r\u0002\u00e9~wsr\u0000\u0011java.l"
+"ang.Boolean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002\u0000\u0001Z\u0000\u0005valuexp\u0000psq\u0000~\u0000\u0000\u0000\u00cb\u00f9\u00dfq\u0000~\u0000\u0011p\u0000sq\u0000~\u0000\r\u0000\u00cb\u00f9"
+"\u00d4ppsr\u0000 com.sun.msv.grammar.OneOrMoreExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001ccom.su"
+"n.msv.grammar.UnaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\u0003expq\u0000~\u0000\u0003xq\u0000~\u0000\u0004\u0000\u00cb\u00f9\u00c9q\u0000~\u0000\u0011p"
+"sr\u0000 com.sun.msv.grammar.AttributeExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000\u0003L\u0000"
+"\tnameClassq\u0000~\u0000\u0001xq\u0000~\u0000\u0004\u0000\u00cb\u00f9\u00c6q\u0000~\u0000\u0011psr\u00002com.sun.msv.grammar.Expre"
+"ssion$AnyStringExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0004\u0000\u0000\u0000\bsq\u0000~\u0000\u0010\u0001psr\u0000 co"
+"m.sun.msv.grammar.AnyNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.gra"
+"mmar.NameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.sun.msv.grammar.Expressio"
+"n$EpsilonExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0004\u0000\u0000\u0000\tq\u0000~\u0000\u001bpsr\u0000#com.sun.ms"
+"v.grammar.SimpleNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\tlocalNamet\u0000\u0012Ljava/lan"
+"g/String;L\u0000\fnamespaceURIq\u0000~\u0000\"xq\u0000~\u0000\u001dt\u0000\u0012GO.go_jaxb.Versiont\u0000+h"
+"ttp://java.sun.com/jaxb/xjc/dummy-elementssq\u0000~\u0000\u0000\u0002\u001d\u0084\u0096q\u0000~\u0000\u0011p\u0000s"
+"q\u0000~\u0000\u0007\u0002\u001d\u0084\u008bppsq\u0000~\u0000\u0000\u0000\u00cb\u00f9\u00dfpp\u0000sq\u0000~\u0000\r\u0000\u00cb\u00f9\u00d4ppsq\u0000~\u0000\u0014\u0000\u00cb\u00f9\u00c9q\u0000~\u0000\u0011psq\u0000~\u0000\u0017\u0000\u00cb"
+"\u00f9\u00c6q\u0000~\u0000\u0011pq\u0000~\u0000\u001aq\u0000~\u0000\u001eq\u0000~\u0000 sq\u0000~\u0000!t\u0000\u0016GO.go_jaxb.VersionTypeq\u0000~\u0000%s"
+"q\u0000~\u0000\r\u0001Q\u008a\u00a7ppsq\u0000~\u0000\u0017\u0001Q\u008a\u009cq\u0000~\u0000\u0011psr\u0000\u001bcom.sun.msv.grammar.DataExp\u0000\u0000"
+"\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Datatype;L\u0000\u0006exceptq\u0000~"
+"\u0000\u0003L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq\u0000~\u0000\u0004\u0000nCuppsr\u0000\"com"
+".sun.msv.datatype.xsd.QnameType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000*com.sun.msv.da"
+"tatype.xsd.BuiltinAtomicType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000%com.sun.msv.datat"
+"ype.xsd.ConcreteType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd."
+"XSDatatypeImpl\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\fnamespaceUriq\u0000~\u0000\"L\u0000\btypeNameq\u0000~\u0000"
+"\"L\u0000\nwhiteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcess"
+"or;xpt\u0000 http://www.w3.org/2001/XMLSchemat\u0000\u0005QNamesr\u00005com.sun."
+"msv.datatype.xsd.WhiteSpaceProcessor$Collapse\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000,"
+"com.sun.msv.datatype.xsd.WhiteSpaceProcessor\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u0000"
+"0com.sun.msv.grammar.Expression$NullSetExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000"
+"xq\u0000~\u0000\u0004\u0000\u0000\u0000\nppsr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tloc"
+"alNameq\u0000~\u0000\"L\u0000\fnamespaceURIq\u0000~\u0000\"xpq\u0000~\u0000;q\u0000~\u0000:sq\u0000~\u0000!t\u0000\u0004typet\u0000)h"
+"ttp://www.w3.org/2001/XMLSchema-instanceq\u0000~\u0000 sq\u0000~\u0000!t\u0000\u0007versio"
+"nt\u0000\u0000q\u0000~\u0000 sq\u0000~\u0000\r\u0002M\typpsq\u0000~\u0000\u0000\u0000\u00cb\u00f9\u00dfpp\u0000sq\u0000~\u0000\r\u0000\u00cb\u00f9\u00d4ppsq\u0000~\u0000\u0014\u0000\u00cb\u00f9\u00c9q\u0000~\u0000"
+"\u0011psq\u0000~\u0000\u0017\u0000\u00cb\u00f9\u00c6q\u0000~\u0000\u0011pq\u0000~\u0000\u001aq\u0000~\u0000\u001eq\u0000~\u0000 sq\u0000~\u0000!t\u0000\u000eGO.go_jaxb.RDFq\u0000~\u0000"
+"%sq\u0000~\u0000\u0000\u0001\u0081\u000f\u0098pp\u0000sq\u0000~\u0000\u0007\u0001\u0081\u000f\u008dppsq\u0000~\u0000\u0000\u0000\u00cb\u00f9\u00dfpp\u0000sq\u0000~\u0000\r\u0000\u00cb\u00f9\u00d4ppsq\u0000~\u0000\u0014\u0000\u00cb\u00f9"
+"\u00c9q\u0000~\u0000\u0011psq\u0000~\u0000\u0017\u0000\u00cb\u00f9\u00c6q\u0000~\u0000\u0011pq\u0000~\u0000\u001aq\u0000~\u0000\u001eq\u0000~\u0000 sq\u0000~\u0000!t\u0000\u0012GO.go_jaxb.RD"
+"FTypeq\u0000~\u0000%sq\u0000~\u0000\r\u0000\u00b5\u0015\u00a9ppsq\u0000~\u0000\u0017\u0000\u00b5\u0015\u009eq\u0000~\u0000\u0011pq\u0000~\u00003sq\u0000~\u0000!q\u0000~\u0000Dq\u0000~\u0000Eq"
+"\u0000~\u0000 sq\u0000~\u0000!t\u0000\u0003RDFq\u0000~\u0000Hsq\u0000~\u0000\r\u0001\u00cb\u0085<ppsq\u0000~\u0000\u0017\u0001\u00cb\u00851q\u0000~\u0000\u0011psr\u0000\u001ccom.sun"
+".msv.grammar.ValueExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002dtq\u0000~\u00001L\u0000\u0004nameq\u0000~\u00002L\u0000\u0005val"
+"uet\u0000\u0012Ljava/lang/Object;xq\u0000~\u0000\u0004\u0000\u00e1\u00ee\u00afppsr\u0000#com.sun.msv.datatype."
+"xsd.StringType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001Z\u0000\risAlwaysValidxq\u0000~\u00005q\u0000~\u0000:t\u0000\u0006strin"
+"gsr\u00005com.sun.msv.datatype.xsd.WhiteSpaceProcessor$Preserve\u0000\u0000"
+"\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000=\u0001sq\u0000~\u0000Aq\u0000~\u0000dq\u0000~\u0000:t\u0000+http://www.w3.org/1999/02"
+"/22-rdf-syntax-ns#sq\u0000~\u0000!t\u0000\u0003rdfq\u0000~\u0000Hq\u0000~\u0000 sq\u0000~\u0000\r\u0002y\u00f4\u00ebppsq\u0000~\u0000\u0017\u0002y"
+"\u00f4\u00e0q\u0000~\u0000\u0011psq\u0000~\u0000_\u0000\u00e1\u00ee\u00afppq\u0000~\u0000cq\u0000~\u0000gt\u0000(http://www.geneontology.org"
+"/dtds/go.dtd#sq\u0000~\u0000!t\u0000\u0002goq\u0000~\u0000Hq\u0000~\u0000 sq\u0000~\u0000\r\u0001?\u00c2Dppsq\u0000~\u0000\u0017\u0001?\u00c29q\u0000~\u0000"
+"\u0011pq\u0000~\u00003sq\u0000~\u0000!q\u0000~\u0000Dq\u0000~\u0000Eq\u0000~\u0000 sq\u0000~\u0000!t\u0000\u0002goq\u0000~\u0000Hsr\u0000\"com.sun.msv."
+"grammar.ExpressionPool\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lcom/sun/msv/"
+"grammar/ExpressionPool$ClosedHash;xpsr\u0000-com.sun.msv.grammar."
+"ExpressionPool$ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0002\u0000\u0004I\u0000\u0005countI\u0000\tthresholdL\u0000\u0006p"
+"arentq\u0000~\u0000w[\u0000\u0005tablet\u0000![Lcom/sun/msv/grammar/Expression;xp\u0000\u0000\u0000\u0016"
+"\u0000\u0000\u00009pur\u0000![Lcom.sun.msv.grammar.Expression;\u00d68D\u00c3]\u00ad\u00a7\n\u0002\u0000\u0000xp\u0000\u0000\u0000\u00bfp"
+"ppppppppppppppppppppppppq\u0000~\u0000\tpppppppppppppq\u0000~\u0000Qpq\u0000~\u0000\u000fpppq\u0000~\u0000"
+"\u0016q\u0000~\u0000*q\u0000~\u0000Lq\u0000~\u0000Tpppq\u0000~\u0000\u000epppq\u0000~\u0000\u0013q\u0000~\u0000)q\u0000~\u0000Kq\u0000~\u0000Sq\u0000~\u0000\npppppppp"
+"pppppppppppq\u0000~\u0000.pppppppppppppq\u0000~\u0000kppppppppppppppppppppppppq\u0000"
+"~\u0000Ipppppppppppppppppppppppppppppppq\u0000~\u0000qq\u0000~\u0000\'q\u0000~\u0000\u000bppppq\u0000~\u0000Xpp"
+"pq\u0000~\u0000]pppppppppppppq\u0000~\u0000\fpppppppppppppp"));
        }
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends GO.go_jaxb.impl.runtime.AbstractUnmarshallingEventHandlerImpl
    {


        public Unmarshaller(GO.go_jaxb.impl.runtime.UnmarshallingContext context) {
            super(context, "----");
        }

        protected Unmarshaller(GO.go_jaxb.impl.runtime.UnmarshallingContext context, int startState) {
            this(context);
            state = startState;
        }

        public java.lang.Object owner() {
            return GO.go_jaxb.impl.GoImpl.this;
        }

        public void enterElement(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname, org.xml.sax.Attributes __atts)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromEnterElement(___uri, ___local, ___qname, __atts);
                        return ;
                    case  0 :
                        if (("go" == ___local)&&("" == ___uri)) {
                            context.pushAttributes(__atts, false);
                            state = 1;
                            return ;
                        }
                        break;
                    case  1 :
                        attIdx = context.getAttribute("", "rdf");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().enterElement(___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        attIdx = context.getAttribute("", "go");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().enterElement(___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        if (("version" == ___local)&&("" == ___uri)) {
                            spawnHandlerFromEnterElement((((GO.go_jaxb.impl.GoTypeImpl)GO.go_jaxb.impl.GoImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        if (("version" == ___local)&&("" == ___uri)) {
                            spawnHandlerFromEnterElement((((GO.go_jaxb.impl.GoTypeImpl)GO.go_jaxb.impl.GoImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        if (("RDF" == ___local)&&("" == ___uri)) {
                            spawnHandlerFromEnterElement((((GO.go_jaxb.impl.GoTypeImpl)GO.go_jaxb.impl.GoImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        if (("RDF" == ___local)&&("" == ___uri)) {
                            spawnHandlerFromEnterElement((((GO.go_jaxb.impl.GoTypeImpl)GO.go_jaxb.impl.GoImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        break;
                }
                super.enterElement(___uri, ___local, ___qname, __atts);
                break;
            }
        }

        public void leaveElement(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromLeaveElement(___uri, ___local, ___qname);
                        return ;
                    case  2 :
                        if (("go" == ___local)&&("" == ___uri)) {
                            context.popAttributes();
                            state = 3;
                            return ;
                        }
                        break;
                    case  1 :
                        attIdx = context.getAttribute("", "rdf");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().leaveElement(___uri, ___local, ___qname);
                            return ;
                        }
                        attIdx = context.getAttribute("", "go");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().leaveElement(___uri, ___local, ___qname);
                            return ;
                        }
                        break;
                }
                super.leaveElement(___uri, ___local, ___qname);
                break;
            }
        }

        public void enterAttribute(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromEnterAttribute(___uri, ___local, ___qname);
                        return ;
                    case  1 :
                        if (("rdf" == ___local)&&("" == ___uri)) {
                            spawnHandlerFromEnterAttribute((((GO.go_jaxb.impl.GoTypeImpl)GO.go_jaxb.impl.GoImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname);
                            return ;
                        }
                        if (("go" == ___local)&&("" == ___uri)) {
                            spawnHandlerFromEnterAttribute((((GO.go_jaxb.impl.GoTypeImpl)GO.go_jaxb.impl.GoImpl.this).new Unmarshaller(context)), 2, ___uri, ___local, ___qname);
                            return ;
                        }
                        break;
                }
                super.enterAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void leaveAttribute(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  3 :
                        revertToParentFromLeaveAttribute(___uri, ___local, ___qname);
                        return ;
                    case  1 :
                        attIdx = context.getAttribute("", "rdf");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().leaveAttribute(___uri, ___local, ___qname);
                            return ;
                        }
                        attIdx = context.getAttribute("", "go");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().leaveAttribute(___uri, ___local, ___qname);
                            return ;
                        }
                        break;
                }
                super.leaveAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void handleText(final java.lang.String value)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                try {
                    switch (state) {
                        case  3 :
                            revertToParentFromText(value);
                            return ;
                        case  1 :
                            attIdx = context.getAttribute("", "rdf");
                            if (attIdx >= 0) {
                                context.consumeAttribute(attIdx);
                                context.getCurrentHandler().text(value);
                                return ;
                            }
                            attIdx = context.getAttribute("", "go");
                            if (attIdx >= 0) {
                                context.consumeAttribute(attIdx);
                                context.getCurrentHandler().text(value);
                                return ;
                            }
                            break;
                    }
                } catch (java.lang.RuntimeException e) {
                    handleUnexpectedTextException(value, e);
                }
                break;
            }
        }

    }

}
