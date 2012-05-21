package org.levigo.jadice.demo.tiffconverter.controller;

public interface JobListener {
  void jobEnqueued(JobController controller, Job job);
  void jobFinished(JobController controller, Job job);
  void jobFailed(JobController controller, Job job);
}
