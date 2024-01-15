package org.openmrs.module.patientgrid;

import org.openmrs.module.BaseModuleActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientGridActivator extends BaseModuleActivator {

  private static final Logger log = LoggerFactory.getLogger(PatientGridActivator.class);

  /**
   * @see BaseModuleActivator#started()
   */
  @Override
  public void started() {
    log.info("Patient grid module started");
  }

  /**
   * @see BaseModuleActivator#stopped()
   */
  @Override
  public void stopped() {
    log.info("Patient grid module stopped");
  }

}
