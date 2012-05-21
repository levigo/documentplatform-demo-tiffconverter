package org.levigo.jadice.demo.tiffconverter.ui.jobtree;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.levigo.jadice.demo.tiffconverter.controller.Job;
import org.levigo.jadice.demo.tiffconverter.ui.UIUtils;

import com.levigo.util.base.Disposable;
import com.levigo.util.swing.flextree.DynamicTreeModule;
import com.levigo.util.swing.flextree.TreeIconProvider;
import com.levigo.util.swing.flextree.TreeLabelProvider;

public class JobModule implements TreeLabelProvider, TreeIconProvider, DynamicTreeModule {

  private static final Icon JOB_ENQUEUED = UIUtils.ICONS.getIcon("job.enqueued");
  private static final Icon JOB_ERROR = UIUtils.ICONS.getIcon("job.error");
  private static final Icon JOB_FINISHED = UIUtils.ICONS.getIcon("job.finished");
  private static final Icon JOB_RUNNING = UIUtils.ICONS.getIcon("job.running");

  private final class JobChangeBridge implements Disposable, PropertyChangeListener {

    private final Job job;
    private final ChangeListener changeListener;


    public JobChangeBridge(Job job, ChangeListener changeListener) {
      super();
      this.job = job;
      this.changeListener = changeListener;
      job.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      changeListener.nodeRefreshRequired();
    }

    @Override
    public void dispose() {
      job.removePropertyChangeListener(this);
    }


  }

  @Override
  public void updateLabel(TreePath path, StyledDocument doc) throws BadLocationException {
    if (path.getLastPathComponent() instanceof Job) {
      String name = ((Job) path.getLastPathComponent()).getDocument().getName();

      doc.insertString(doc.getLength(), name, null);

      String statusText = null;
      SimpleAttributeSet attributes = new SimpleAttributeSet(doc.getCharacterElement(doc.getLength()).getAttributes());

      switch (((Job) path.getLastPathComponent()).getState()){
        case ENQUEUED :
          statusText = "ENQUEUED";
          StyleConstants.setForeground(attributes, Color.BLUE);
          break;
        case ERROR :
          statusText = "ERROR";
          StyleConstants.setForeground(attributes, Color.RED);
          break;
        case FINISHED :
          statusText = "FINISHED";
          StyleConstants.setForeground(attributes, Color.GREEN);
          break;
        case RUNNING :
          statusText = "RUNNING";
          StyleConstants.setForeground(attributes, Color.CYAN);
          break;
      }
      
      doc.insertString(doc.getLength(), " ["+statusText+"]", attributes);

    }
  }

  @Override
  public Icon getIcon(TreePath path) {
    if (path.getLastPathComponent() instanceof Job) {
      switch (((Job) path.getLastPathComponent()).getState()){
        case ENQUEUED :
          return JOB_ENQUEUED;
        case ERROR :
          return JOB_ERROR;
        case FINISHED :
          return JOB_FINISHED;
        case RUNNING :
          return JOB_RUNNING;
      }
    }
    return null;
  }

  @Override
  public Disposable registerChangeListener(TreePath path, ChangeListener changeListener) {
    if (path.getLastPathComponent() instanceof Job)
      return new JobChangeBridge((Job) path.getLastPathComponent(), changeListener);
    return null;
  }

}
