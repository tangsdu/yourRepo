package com.cignacmb.iuss.web.common.util;

import java.io.StringWriter;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 利用注解实现xml与对象互转
 *
 * @author r6yuxx
 */
public class XStreamUtil {

    /**
     * t->xml
     *
     * @param obj
     * @return
     */
    public static String toXML(Object obj) {
        XStream xStream = new XStream(new XppDriver(new NoNameCoder()));
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.autodetectAnnotations(true);
        xStream.ignoreUnknownElements();
        return toXML(obj, true);
    }

    public static String toXML(Object obj, boolean pretty) {
        XStream xStream = new XStream(new XppDriver(new NoNameCoder()));
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.autodetectAnnotations(true);
        xStream.ignoreUnknownElements();
        if (!pretty) {
            Writer writer = new StringWriter();
            xStream.marshal(obj, new CompactWriter(writer));
            return writer.toString();
        }
        return xStream.toXML(obj);
    }

    /**
     * xml->t
     *
     * @param xmlStr
     * @param cls
     * @return
     */
    public static <T> T toBean(String xmlStr, Class<T> cls) {
        XStream xStream = new XStream(new XppDriver(new NoNameCoder()));
        xStream.processAnnotations(cls);
        xStream.ignoreUnknownElements();
        @SuppressWarnings("unchecked")
        T t = (T) xStream.fromXML(xmlStr);
        return t;
    }
}
