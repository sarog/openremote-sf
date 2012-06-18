package org.openremote.controller.protocol.enocean.datatype;


import java.math.BigDecimal;
import java.util.List;

public abstract class CategoricalScale
{

  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * TODO
   */
  List<ScaleCategory> categories;


  // Constructors ---------------------------------------------------------------------------------

  public CategoricalScale(List<ScaleCategory> categories)
  {
    this.categories = categories;
  }


  public ScaleCategory scaleRawValue(int value)
  {
    for(ScaleCategory category : categories)
    {
      if(category.isInCategory(value))
      {
        return category;
      }
    }

    return null;
  }
}
