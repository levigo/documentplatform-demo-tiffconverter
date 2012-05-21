package org.levigo.jadice.demo.tiffconverter.controller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.levigo.jadice.demo.tiffconverter.controller.Job.State;

import com.levigo.jadice.document.Document;
import com.levigo.jadice.tiffconverter.TiffConvertConfiguration;
import com.levigo.jadice.tiffconverter.TiffConvertRenderSettings.Compression;
import com.levigo.jadice.tiffconverter.TiffConverter;
import com.levigo.util.concurrent.tasks.Task;


/**
 * Convert a given {@link Document} to a TIFF file.
 */
public class ConvertDocumentTask implements Task<Job> {

  static {
    // Due to a bug to the jadice document platform versions up to 5.1.0.3 (will be fixed with
    // 5.1.0.4) this line has to be called to ensure proper initialization. This line must be
    // removed after upgrading to a release later than 5.1.0.3
    com.levigo.jadice.document.internal.codec.tiff.TagGroup.values();
  }

  private final Job job;


  public ConvertDocumentTask(Job job) {
    super();
    if (job == null)
      throw new IllegalArgumentException("job must not be null");
    
    this.job = job;
  }


  public Job getJob() {
    return job;
  }
  
  @Override
  public Job call() throws Exception {
    try {
      job.setState(State.RUNNING);
      Job result = doConvert();
      job.setState(State.FINISHED);
      return result;
    } catch (Exception e) {
      job.setState(State.ERROR);
      throw e;
    }
  }


  protected Job doConvert() throws FileNotFoundException, IOException {
    Document document = job.getDocument();
    FileOutputStream out = new FileOutputStream(job.getTargetFile());

    TiffConvertConfiguration convertConfiguration = new TiffConvertConfiguration();

    // compression AUTO means that the tiff converter will use the most appropriate compression
    // method. That is CCITT for B/W and JPEG for color.
    convertConfiguration.setCompression(Compression.AUTO);

    TiffConverter converter = new TiffConverter(convertConfiguration);

    // the conversion must be surrounded by at least a read lock, to avoid that concurrent
    // modifications cause the conversion to fail.
    document.getPages().getReadWriteLock().readLock().lock();
    try {
      converter.convertToTiff(document, out, false);
    } finally {
      document.getPages().getReadWriteLock().readLock().unlock();
    }
    out.flush();
    out.close();

    return job;
  }

}
