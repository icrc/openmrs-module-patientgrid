package org.openmrs.module.patientgrid;

import org.junit.runner.notification.RunNotifier;
import org.powermock.modules.junit4.PowerMockRunner;

public class SafePowerMockRunner extends PowerMockRunner {

  public SafePowerMockRunner(Class<?> klass) throws Exception {
    super(klass);
  }

  @Override
  public void run(RunNotifier notifier) {
    try {
      super.run(notifier);
    } catch (RuntimeException err) {
      if (err.getCause() instanceof java.lang.NoSuchFieldException
          && err.getCause().getMessage().equals("modifiers")) {
        // on JDK12 you cannot change 'modifiers'
      } else {
        throw err;
      }
    }
  }

}
