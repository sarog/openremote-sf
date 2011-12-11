package org.openremote.modeler.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.modeler.domain.KnxGroupAddress;

public class KnxImporter {

    public List<KnxGroupAddress> importETSConfiguration(InputStream inputStream) {

        List<KnxGroupAddress> result = new ArrayList<KnxGroupAddress>();
        String xmlData = null;
        DecimalFormat df = new DecimalFormat("000");
        SAXBuilder builder = new SAXBuilder();
        Document document = null ;
        
        try {
            ZipInputStream zin = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zin.getNextEntry();
            while (zipEntry != null) {
               if (zipEntry.getName().endsWith("/0.xml")) {
                  xmlData = convertStreamToString(zin);
                  break;
               }
               zipEntry = zin.getNextEntry();
            }
            
            if (xmlData != null) {
               // parse the XML as a W3C Document
               StringReader in = new StringReader(xmlData);
               document = builder.build(in);

               // Query all GroupAddress elements
               XPath xpath = XPath.newInstance("//knx:GroupAddress");
               xpath.addNamespace("knx", "http://knx.org/xml/project/10");
               List<Element> xresult = xpath.selectNodes(document);
               for (Element element : xresult) {

                  String id = element.getAttributeValue("Id");
                  String name = element.getAttributeValue("Name");
                  String address = element.getAttributeValue("Address");
                  String dpt = null; 
                  // Query referenced ComObjectInstanceRef element which holds DPT
                  xpath = XPath.newInstance("//knx:Send[@GroupAddressRefId='"+ id + "']/../..");
                  xpath.addNamespace("knx", "http://knx.org/xml/project/10");
                  List<Element> result2 = xpath.selectNodes(document);
                  if (result2.size() > 0) {
                     dpt = result2.get(0).getAttributeValue("DatapointType");
                     if (dpt != null && StringUtils.isNotEmpty(dpt)) {
                        StringTokenizer st = new StringTokenizer(dpt, "-");
                        st.nextElement();
                        dpt = st.nextToken() + "." + df.format(Integer.parseInt(st.nextToken()));
                     } else {
                        dpt = null;
                     }
                  }
                  String levelAddress = getAddressFromInt(Integer.parseInt(address));
                  result.add(new KnxGroupAddress(dpt, levelAddress, name));
                  System.out.println(levelAddress + " - " + name + " - " + dpt);
               }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;
    }

    
    

    public static String convertStreamToString(InputStream is) throws IOException {
       if (is != null) {
          Writer writer = new StringWriter();

          char[] buffer = new char[1024];
          try {
             Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
             int n;
             while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
             }
          } finally {
             is.close();
          }
          return writer.toString();
       } else {
          return "";
       }
    }

    public static String getAddressFromInt(int knxaddress) {
       int maingroup, subgroup, group;
       // extract values
       maingroup = (knxaddress >> 11) & 0x0f;
       subgroup = (knxaddress >> 8) & 0x07;
       group = knxaddress & 0xff;
       String erg = "" + maingroup + "/" + subgroup + "/" + group;
       return erg;
    }
}
