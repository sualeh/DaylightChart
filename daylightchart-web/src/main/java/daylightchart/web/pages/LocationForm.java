/*
 * Copyright 2007-2008, Sualeh Fatehi <sualeh@hotmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution 3.0 License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by/3.0/ 
 * or send a letter to 
 * Creative Commons
 * 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package daylightchart.web.pages;


import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

final class LocationForm
  extends Form
{

  private static final long serialVersionUID = 2682300618749680498L;

  private final boolean isInEditMode;

  LocationForm(final String id, final IModel model, final boolean isInEditMode)
  {
    super(id, model);

    this.isInEditMode = isInEditMode;
  }

  @Override
  protected void onSubmit()
  {
  }

}
