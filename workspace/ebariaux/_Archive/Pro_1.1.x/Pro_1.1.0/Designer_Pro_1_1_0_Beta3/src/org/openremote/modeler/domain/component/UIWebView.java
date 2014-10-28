package org.openremote.modeler.domain.component;

import javax.persistence.Transient;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;
import org.openremote.modeler.utils.StringUtils;

public class UIWebView extends UIComponent implements SensorOwner, SensorLinkOwner {
	
	private static final long serialVersionUID = -4815544266807672921L;
	
	private String url = "";
	private String userid = "";
	private String password = "";
	
	
	   /** The ui label can change display image by the sensor. */
	   private Sensor sensor;

	   private SensorWithInfoDTO sensorDTO;

	   /** The sensor link is for recording the properties of the sensor. */
	   private SensorLink sensorLink;

	   public UIWebView(long oid) {
	      super(oid);
	   }

	   public UIWebView() {
	   }

	   public UIWebView(String url, String userid,String password, String color, int fontSize, Sensor sensor) {
	      this.url = url;
	      this.userid=userid;
	      this.password=password;
	      this.sensor = sensor;
	      if (sensor != null) {
	         this.sensorLink = new SensorLink(sensor);
	      } else {
	         sensorLink.clear();
	      }
	   }

	   public UIWebView(UIWebView webview) {
	      setOid(webview.getOid());
	      this.url = webview.url;
	      this.userid=webview.userid;
	      this.password=webview.password;
	      this.sensor = webview.sensor;
	      this.sensorLink = webview.sensorLink;
	   }

	   public String getURL() {
	      return url;
	   }

	   public String getUserName() {
		      return userid;
		   }
	   
	   public String getPassword() {
		      return password;
		   }
	   
	   public void setURL(String url) {
	      this.url = url;
	   }

	   public void setUserName(String userid) {
		      this.userid = userid;
		   }
	   
	   public void setPassword(String password) {
		      this.password = password;
		   }   
	 
	   public Sensor getSensor() {
	      return sensor;
	   }

	   public void setSensor(Sensor sensor) {
	      this.sensor = sensor;
	   }

	   public void setSensorAndInitSensorLink(Sensor sensor) {
	      this.sensor = sensor;
	      if (sensor != null) {
	         this.sensorLink = new SensorLink(sensor);
	      } else {
	         sensorLink.clear();
	      }
	   }
	   
	   public SensorLink getSensorLink() {
	      return sensorLink;
	   }

	   public void setSensorLink(SensorLink sensorLinker) {
	      this.sensorLink = sensorLinker;
	   }

     public SensorWithInfoDTO getSensorDTO() {
       return sensorDTO;
     }

     public void setSensorDTO(SensorWithInfoDTO sensorDTO) {
       this.sensorDTO = sensorDTO;
     }
     
     public void setSensorDTOAndInitSensorLink(SensorWithInfoDTO sensorDTO) {
       this.sensorDTO = sensorDTO;
       if (sensorDTO != null) {
          this.sensorLink = new SensorLink();
          this.sensorLink.setSensorDTO(sensorDTO);
       } else {
          sensorLink.clear();
       }
    }

	   @Transient
	   @Override
	   public String getPanelXml() {
	      StringBuilder sb = new StringBuilder();
	      sb.append("<web id=\"" + getOid() + "\" src=\"" + StringUtils.escapeXml(url)
	            + "\" username=\"" + StringUtils.escapeXml(userid) + "\" password=\"" + StringUtils.escapeXml(password)	            
	            + "\">\n");
	      if (sensor != null) {
	         sb.append(sensorLink.getXMLString());
	      }
	      sb.append("</web>");
	      return sb.toString();
	   }

	   @Override
	   public String getName() {
	      return "WebView";
	   }

	   @Transient
	   public String getDisplayName() {
	      int maxLength = 20;
	      if (url.length() > maxLength) {
	         return url.substring(0, maxLength) + "...";
	      }
	      return url;
	   }

	   @Override
	   public int getPreferredWidth() {
	      return 150;
	   }

	   @Override
	   public int getPreferredHeight() {
	      return 50;
	   }
}
