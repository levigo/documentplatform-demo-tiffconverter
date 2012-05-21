package org.levigo.jadice.demo.tiffconverter.controller;

public class JobAdapter implements JobListener {

  @Override
  public void jobEnqueued(JobController controller, Job job) {
    jobStateChanged(controller, job);
  }

  @Override
  public void jobFinished(JobController controller, Job job) {
    jobStateChanged(controller, job);
  }

  @Override
  public void jobFailed(JobController controller, Job job) {
    jobStateChanged(controller, job);
  }

  protected void jobStateChanged(JobController controller, Job job) {
  }
  
}
