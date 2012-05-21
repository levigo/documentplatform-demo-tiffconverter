package org.levigo.jadice.demo.tiffconverter.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import com.levigo.jadice.document.Document;

public class Job {

  public enum State {
    ENQUEUED, RUNNING, FINISHED, ERROR
  }

  private final PropertyChangeSupport propertyChangeSupport;
  private final Document document;
  private final File targetFile;
  private volatile State state = State.ENQUEUED;

  public Job(Document document, File targetFile) {
    super();
    if (document == null)
      throw new IllegalArgumentException("document must not be null");
    if (targetFile == null)
      throw new IllegalArgumentException("targetFile must not be null");
    this.document = document;
    this.targetFile = targetFile;
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public Document getDocument() {
    return document;
  }

  public File getTargetFile() {
    return targetFile;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    propertyChangeSupport.firePropertyChange("state", this.state, this.state = state);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    if (listener == null)
      throw new IllegalArgumentException("listener must not be null");
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    if (listener == null)
      throw new IllegalArgumentException("listener must not be null");
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (listener == null)
      throw new IllegalArgumentException("listener must not be null");
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (listener == null)
      throw new IllegalArgumentException("listener must not be null");
    propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
  }

}
