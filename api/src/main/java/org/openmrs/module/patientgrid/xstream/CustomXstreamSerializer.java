package org.openmrs.module.patientgrid.xstream;

import com.thoughtworks.xstream.XStream;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.serialization.SerializationException;
import org.openmrs.serialization.SimpleXStreamSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Custom Xstream Serializer to persist only the minimum data form SimpleDataSet. SimpleDataSet must
 * contain only the data used by the front-end
 */
public class CustomXstreamSerializer extends SimpleXStreamSerializer {

  private static final int DEFAULT_BUFFER_SIZE = 4 * 8192;

  public CustomXstreamSerializer() throws SerializationException {
    init();
  }

  public void toXML(Object value, File target) throws IOException {
    File parent = target.getParentFile();
    if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
      throw new IOException("Directory '" + parent + "' could not be created");
    }
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(Files.newOutputStream(target.toPath()), StandardCharsets.UTF_8),
        DEFAULT_BUFFER_SIZE)) {
      xstream.toXML(value, writer);
    }
  }

  public Object fromXML(File targetFile) throws IOException {
    try (final InputStreamReader buffered = new InputStreamReader(
        new BufferedInputStream(Files.newInputStream(targetFile.toPath()), DEFAULT_BUFFER_SIZE),
        StandardCharsets.UTF_8)) {
      return xstream.fromXML(buffered);
    }
  }

  private void init() {
    //serialize only the field idToRowMap
    xstream.omitField(SimpleDataSet.class, "definition");
    xstream.omitField(SimpleDataSet.class, "sortCriteria");
    xstream.omitField(SimpleDataSet.class, "context");
    xstream.omitField(SimpleDataSet.class, "metaData");
    //secure xstream
    XStream.setupDefaultSecurity(xstream);
    xstream.allowTypesByWildcard(new String[]{"org.openmrs.**"});
    //do stuff on xstream
  }
}
