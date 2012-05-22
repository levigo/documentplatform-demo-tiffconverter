package org.levigo.jadice.demo.tiffconverter.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.levigo.jadice.demo.tiffconverter.controller.Job.State;

import com.levigo.jadice.document.Document;
import com.levigo.util.base.glazedlists.BasicEventList;
import com.levigo.util.base.glazedlists.EventList;
import com.levigo.util.concurrent.tasks.Task;
import com.levigo.util.concurrent.tasks.TaskExecutor;
import com.levigo.util.concurrent.tasks.TaskScope;
import com.levigo.util.concurrent.tasks.ThreadPoolTaskService;

public class JobController {

  private volatile File targetDirectory;
  private final TaskExecutor<Job> executor;
  private final EventList<Job> jobList;
  private final CopyOnWriteArrayList<JobListener> jobListeners;
  private final PropertyChangeSupport propertyChangeSupport;

  public JobController() {
    propertyChangeSupport = new PropertyChangeSupport(this);
    jobListeners = new CopyOnWriteArrayList<JobListener>();
    executor = ThreadPoolTaskService.getDefault().getExecutor(new TaskScope<Job>() {

      @Override
      public void taskCompleted(Job job) {
        fireJobFinished(job);
      }

      @Override
      public void taskFailed(Task<Job> task, Throwable cause) {
        // we know that the instance we are facing is a ConvertDocumentTask. So it is safe to cast
        // here.
        ConvertDocumentTask convertTask = (ConvertDocumentTask) task;
        convertTask.getJob().setState(State.ERROR);
        fireJobFailed(convertTask.getJob());
      }
    });
    jobList = new BasicEventList<Job>();

    targetDirectory = new File(System.getProperty("user.home"));
  }

  public File getTargetDirectory() {
    return targetDirectory;
  }

  public void setTargetDirectory(File targetDirectory) {
    propertyChangeSupport.firePropertyChange("targetDirectory", this.targetDirectory, this.targetDirectory = targetDirectory);
  }

  public void enqueue(Document document) {

    File targetFile = null;
    if (targetDirectory != null) {
      targetDirectory.mkdirs();
      targetFile = new File(targetDirectory, document.getName() + ".tif");
    } else {
      try {
        targetFile = File.createTempFile("jadice-documentplatform-tiffconverter-", ".tif");
      } catch (IOException e) {
        throw new RuntimeException("Failed to generate temporary file. Specify the output file explicity.", e);
      }
    }

    Job job = new Job(document, targetFile);

    jobList.getReadWriteLock().writeLock().lock();
    try {
      jobList.add(job);
    } finally {
      jobList.getReadWriteLock().writeLock().unlock();
    }
    fireJobEnqueued(job);
    executor.execute(new ConvertDocumentTask(job));

  }

  public EventList<Job> getJobList() {
    return jobList;
  }

  public void addJobListener(JobListener listener) {
    if (listener == null)
      throw new IllegalArgumentException("listener must not be null");

    jobListeners.add(listener);
  }

  public void removeJobListener(JobListener listener) {
    if (listener == null)
      throw new IllegalArgumentException("listener must not be null");

    jobListeners.remove(listener);
  }

  protected void fireJobEnqueued(Job job) {
    for (JobListener listener : jobListeners) {
      listener.jobEnqueued(this, job);
    }
  }

  protected void fireJobFinished(Job job) {
    for (JobListener listener : jobListeners) {
      listener.jobFinished(this, job);
    }
  }

  protected void fireJobFailed(Job job) {
    for (JobListener listener : jobListeners) {
      listener.jobFailed(this, job);
    }
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
