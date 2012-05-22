package org.levigo.jadice.demo.tiffconverter.ui;

import java.awt.Component;

import org.levigo.jadice.demo.tiffconverter.controller.JobController;

import com.levigo.jadice.document.BasicDocument;
import com.levigo.jadice.document.Document;
import com.levigo.jadice.document.UIDocument;
import com.levigo.jadice.swing.thumbnailview.ThumbnailView;
import com.levigo.util.base.Strings;
import com.levigo.util.swing.action.injection.Argument;
import com.levigo.util.swing.action.injection.InjectedCommand;

public class CreateTaskCommand extends InjectedCommand {

  private JobController controller;
  private ThumbnailView thumbnailView;


  public JobController getController() {
    return controller;
  }

  public ThumbnailView getThumbnailView() {
    return thumbnailView;
  }

  /**
   * Direct calls to this method are deprecated. It is used for the argument injection process of
   * {@link InjectedCommand}.
   * 
   * @param controller
   */
  @Deprecated
  @Argument
  public void setController(JobController controller) {
    this.controller = controller;
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


  @Override
  protected boolean canExecute() {
    UIDocument<Component> doc = thumbnailView.getDocument();
    return doc != null && doc.getPageCount() > 0 && !Strings.empty(doc.getName());
  }

  @Override
  protected void execute() {

    Document doc = thumbnailView.getDocument();
    thumbnailView.setDocument(new BasicDocument(""));
    thumbnailView.getSelectionModel().clearSelection();

    controller.enqueue(doc);
  }

}
