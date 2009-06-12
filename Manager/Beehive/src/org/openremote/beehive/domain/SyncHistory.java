/**
 * 
 */
package org.openremote.beehive.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openremote.beehive.utils.DateUtil;

/**
 * @author Tomsky
 *
 */
@Entity
@Table(name = "sync_history")
@SuppressWarnings("serial")
public class SyncHistory extends BusinessEntity {
   private Date startTime;
   private Date endTime;
   private String type;
   private String status;
   private String logPath;
   
   @Column(name = "start_time")
   public Date getStartTime() {
      return startTime;
   }
   @Column(name = "end_time")
   public Date getEndTime() {
      return endTime;
   }
   @Column(nullable = false)
   public String getType() {
      return type;
   }
   @Column(nullable = false)
   public String getStatus() {
      return status;
   }
   @Column(name = "log_path")
   public String getLogPath() {
      return logPath;
   }

   public void setStartTime(Date startTime) {
      this.startTime = startTime;
   }
   
   @Transient
   public String getStartDate() {
      return DateUtil.getDefaultFormat(startTime);
   }
   
   
   public void setEndTime(Date endTime) {
      this.endTime = endTime;
   }
   
   public void setType(String type) {
      this.type = type;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setLogPath(String logPath) {
      this.logPath = logPath;
   }

   public SyncHistory() {
      // TODO Auto-generated constructor stub
   }
}
