package org.levigo.jadice.demo.tiffconverter.ui.jobtree;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.levigo.jadice.demo.tiffconverter.controller.JobController;

import com.levigo.util.base.glazedlists.EventList;
import com.levigo.util.swing.flextree.TreeContentProvider;
import com.levigo.util.swing.flextree.TreeLabelProvider;

public class JobControllerTreeModule implements TreeContentProvider, TreeLabelProvider {
  @Override
  public boolean hasChildren(TreePath path) {
    return path.getLastPathComponent() instanceof JobController;
  }

  @Override
  public EventList<? extends Object> getChildren(TreePath path) {
    // we will only get asked for children if hasChildren returned true
    return ((JobController) path.getLastPathComponent()).getJobList();
  }

  @Override
  public void updateLabel(TreePath path, StyledDocument doc) throws BadLocationException {
    if (path.getLastPathComponent() instanceof JobController) {
      doc.insertString(doc.getLength(), "Jobs", null);
    }
  }
}