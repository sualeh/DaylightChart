package daylightchart.web.pages;


import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.model.Model;

public class ClasspathImage
  extends Image
{

  private static final long serialVersionUID = 5098610119139143212L;

  public ClasspathImage(String id, String resource)
  {
    super(id, new Model(resource));
  }

  @Override
  protected Resource getImageResource()
  {
    return new DynamicImageResource()
    {
      @Override
      protected byte[] getImageData()
      {
        String resource = (String) getModelObject();
        BufferedImage bufferedImage;
        try
        {
          bufferedImage = ImageIO.read(ClasspathImage.class
            .getResourceAsStream(resource));

        }
        catch (IOException e)
        {
          bufferedImage = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        }
        return toImageData(bufferedImage);
      }
    };
  }

}
