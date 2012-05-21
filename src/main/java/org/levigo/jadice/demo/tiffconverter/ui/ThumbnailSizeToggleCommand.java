package org.levigo.jadice.demo.tiffconverter.ui;

import java.awt.Dimension;

import com.levigo.jadice.swing.thumbnailview.ThumbnailView;
import com.levigo.util.swing.action.injection.Argument;
import com.levigo.util.swing.action.injection.InjectedCommand;
import com.levigo.util.swing.action.injection.Parameter;

public class ThumbnailSizeToggleCommand extends InjectedCommand {

  private static final Dimension DEFAULT_SIZE = ThumbnailView.DEFAULT_THUMBNAIL_SIZE;

  public static enum ThumbnailSize {
    SMALL(0.7d), NORMAL(1.0d), LARGE(1.5d);

    private final Dimension size;

    private ThumbnailSize(double resizeFactor) {
      size = new Dimension((int) (DEFAULT_SIZE.width * resizeFactor), (int) (DEFAULT_SIZE.height * resizeFactor));
    }

    public Dimension getSize() {
      return size;
    }
  }

  private ThumbnailView thumbnailView;
  private ThumbnailSize thumbnailSize;

  public ThumbnailSizeToggleCommand() {
    // TODO Auto-generated constructor stub
  }

  public ThumbnailView getThumbnailView() {
    return thumbnailView;
  }

  /**
   * Direct calls to this method are deprecated. It is used for the argument injection process of
   * {@link InjectedCommand}.
   * 
   * @param thumbnailView
   */
  @Deprecated
  @Argument
  public void setThumbnailView(ThumbnailView thumbnailView) {
    this.thumbnailView = thumbnailView;
  }

  /**
   * Direct calls to this method are deprecated. It is used by the parameter processing.
   * 
   * @param thumbnailSize
   */
  @Deprecated
  @Parameter
  public void setThumbnailSize(ThumbnailSize thumbnailSize) {
    this.thumbnailSize = thumbnailSize;
  }

  public ThumbnailSize getThumbnailSize() {
    return thumbnailSize;
  }

  @Override
  protected void execute() {
    thumbnailView.setThumbnailSize(thumbnailSize.getSize());
  }

  @Override
  protected boolean isSelected() {

    Dimension thumbSize = thumbnailView.getThumbnailSize();

    return thumbSize.equals(thumbnailSize.getSize());

  }

}
