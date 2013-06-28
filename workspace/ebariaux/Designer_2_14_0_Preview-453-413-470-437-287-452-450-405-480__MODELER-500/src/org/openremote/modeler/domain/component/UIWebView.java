package org.openremote.modeler.domain.component;

import javax.persistence.Transient;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.domain.ConfigurationFilesGenerationContext;
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

	   public UIWebView(String url, String userid, String password, Sensor sensor) {
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
	      this.sensorDTO = webview.sensorDTO;
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
	   public String getPanelXml(ConfigurationFilesGenerationContext context) {
	      StringBuilder sb = new StringBuilder();
	      sb.append("<web id=\"" + getOid() + "\" src=\"" + StringUtils.escapeXml(url)
	            + "\" username=\"" + StringUtils.escapeXml(userid) + "\" password=\"" + StringUtils.escapeXml(password)	            
	            + "\">\n");
	      if (getSensorDTO() != null) {
	         sb.append("<link type=\"sensor\" ref=\"" + getSensorDTO().getOffsetId() + "\"/>");
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((sensorDTO == null) ? 0 : sensorDTO.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    result = prime * result + ((userid == null) ? 0 : userid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    UIWebView other = (UIWebView) obj;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    if (sensorDTO == null) {
      if (other.sensorDTO != null)
        return false;
    } else if (!sensorDTO.equals(other.sensorDTO))
      return false;
    if (url == null) {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    if (userid == null) {
      if (other.userid != null)
        return false;
    } else if (!userid.equals(other.userid))
      return false;
    return true;
  }

}
