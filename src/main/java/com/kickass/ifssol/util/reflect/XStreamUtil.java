package com.kickass.ifssol.util.reflect;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import ifs.fnd.ap.RecordCollection;

import java.io.*;

public class XStreamUtil {
    public void write(Object o) {
        XStream xstream = new XStream(new StaxDriver());
        String xml = xstream.toXML(o);
        System.out.println(xml);
        try {
            FileOutputStream fos = new FileOutputStream("C:\\temp\\rc.xml");
            fos.write(xml.getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object read() {
        XStream xstream = new XStream(new StaxDriver());

        try {
            ObjectInputStream objectInputStream = xstream.createObjectInputStream(
                    new FileInputStream("C:\\temp\\rc.xml"));
            RecordCollection rc = (RecordCollection) objectInputStream.readObject();
            return rc;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
