/**
 * 
 */
package org.openremote.beehive.repo;

/**
 * @author Tomsky
 *
 */
public class ChangeCount {
   private int addedItemsCount;
   private int modifiedItemsCount;
   private int deletedItemsCount;
   
   public ChangeCount(int addedItemsCount, int modifiedItemsCount, int deletedItemsCount) {
      this.addedItemsCount = addedItemsCount;
      this.modifiedItemsCount = modifiedItemsCount;
      this.deletedItemsCount = deletedItemsCount;
   }
   public int getAddedItemsCount() {
      return addedItemsCount;
   }
   public int getModifiedItemsCount() {
      return modifiedItemsCount;
   }
   public int getDeletedItemsCount() {
      return deletedItemsCount;
   }
}
