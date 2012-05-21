package org.levigo.jadice.demo.tiffconverter.ui;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;

import org.levigo.jadice.demo.tiffconverter.controller.JobController;

import com.levigo.util.swing.action.injection.Argument;
import com.levigo.util.swing.action.injection.InjectedCommand;

public class OpenTargetDirectoryCommand extends InjectedCommand {
  private JobController controller;

  public JobController getController() {
    return controller;
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

  @Override
  protected boolean canExecute() {
    return Desktop.getDesktop().isSupported(Action.OPEN);
  }
  
  @Override
  protected void execute() {
    
    try {
      Desktop.getDesktop().open(controller.getTargetDirectory());
    } catch (IOException e) {
      System.err.println("Failed to open target directory.");
      e.printStackTrace();
    }
  }


}
