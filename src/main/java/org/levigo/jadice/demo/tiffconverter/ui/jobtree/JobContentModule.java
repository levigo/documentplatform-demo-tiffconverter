package org.levigo.jadice.demo.tiffconverter.ui.jobtree;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.levigo.jadice.demo.tiffconverter.controller.Job;

import com.levigo.jadice.document.Document;
import com.levigo.util.base.glazedlists.EventList;
import com.levigo.util.base.glazedlists.GlazedLists;
import com.levigo.util.swing.flextree.TreeContentProvider;
import com.levigo.util.swing.flextree.TreeLabelProvider;

public class JobContentModule implements TreeContentProvider, TreeLabelProvider {

  @Override
  public EventList<? extends Object> getChildren(TreePath path) {
    if (path.getLastPathComponent() instanceof Job) {
      Job job = (Job)path.getLastPathComponent();
      return GlazedLists.eventListOf(job.getDocument(), job.getTargetFile());
    }
    return null;
  }

  @Override
  public boolean hasChildren(TreePath path) {
    return path.getLastPathComponent() instanceof Job;
  }

  @Override
  public void updateLabel(TreePath path, StyledDocument doc) throws BadLocationException {
    if (path.getLastPathComponent() instanceof Document) {
      doc.insertString(doc.getLength(), "Source Document", null);
    }
  }
}
