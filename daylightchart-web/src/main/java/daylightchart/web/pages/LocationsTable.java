package daylightchart.web.pages;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import daylightchart.location.Location;
import daylightchart.options.UserPreferences;

public class LocationsTable
  extends DefaultDataTable
{

  private static final class LocationsDataProvider
    extends SortableDataProvider
  {

    private static final long serialVersionUID = -7664388454797401713L;

    @SuppressWarnings("unused")
    public Iterator<Location> iterator(final int first, final int count)
    {
      final SortParam sortParam = getSort();
      List<Location> locations = UserPreferences.getLocations();
      return locations.listIterator(first);
    }

    public IModel model(final Object object)
    {
      return new CompoundPropertyModel(object);
    }

    public int size()
    {
      return UserPreferences.getLocations().size();
    }

  }

  private static final long serialVersionUID = 8016043970738990340L;

  private static IColumn[] getColumnsForLocationsTable()
  {
    final List<IColumn> columns = new ArrayList<IColumn>();
    columns.add(new AbstractColumn(new Model("Edit/ Delete"))
    {
      private static final long serialVersionUID = 6566804574749277918L;

      public void populateItem(final Item cellItem,
                               final String componentId,
                               final IModel rowModel)
      {
        cellItem.add(new LocationEditDeletePanel(componentId, rowModel));
      }
    });
    columns.add(new PropertyColumn(new Model("City"), "city", "city"));
    columns.add(new PropertyColumn(new Model("Country"), "country", "country"));
    columns.add(new PropertyColumn(new Model("Latitude"),
                                   "pointLocation.latitude",
                                   "pointLocation.latitude"));
    columns.add(new PropertyColumn(new Model("Longitude"),
                                   "pointLocation.longitude",
                                   "pointLocation.longitude"));
    return columns.toArray(new IColumn[columns.size()]);
  }

  LocationsTable(final String id, final int itemsPerPage)
  {
    super(id,
          getColumnsForLocationsTable(),
          new LocationsDataProvider(),
          itemsPerPage);
  }

}
