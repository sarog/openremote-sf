package org.openremote.controller.protocol.enocean.datatype;

import java.util.ArrayList;
import java.util.List;

public class BoolScale extends OrdinalScale
{
  private static List<ScaleCategory> createList(ScaleCategory item1, ScaleCategory item2)
  {
    List<ScaleCategory> list = new ArrayList<ScaleCategory>(2);
    list.add(item1);
    list.add(item2);

    return list;
  }

  // Private Instance Fields ----------------------------------------------------------------------

  private ScaleCategory trueCategory;
  private ScaleCategory falseCategory;


  // Constructors ---------------------------------------------------------------------------------

  public BoolScale(ScaleCategory trueCategory, ScaleCategory falseCategory)
  {
    super(createList(trueCategory, falseCategory));

    this.trueCategory = trueCategory;
    this.falseCategory = falseCategory;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public boolean isTrueCategory(ScaleCategory category)
  {
    return category == trueCategory;
  }

  public ScaleCategory getCategory(boolean value)
  {
    return value ? trueCategory : falseCategory;
  }
}
