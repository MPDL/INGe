package de.mpg.mpdl.inge.inge_validation;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationConeCacheConfigException;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ConeCache;

public class TestConeCache {

  public static void main(String[] args) {
    System.out.println("Start");

    final TestConeCache t = new TestConeCache();
    t.go();

    System.out.println("Ende");
  }

  public void go() {
    final List<Thread> list = new ArrayList<Thread>();
    list.add(new GetDDLThread());
    list.add(new GetISOThread());
    list.add(new GetISOIdThread());
    list.add(new GetMIMEThread());
    list.add(new GetMPIPKSThread());
    list.add(new GetMPIRKThread());
    list.add(new GetMPISGROUPThread());
    list.add(new GetMPISPROJECTThread());
    list.add(new RefreshCacheThread());

    for (final Thread thread : list) {
      thread.start();
    }
  }

  class GetDDLThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("DDC_Title: " + this.cache.getDdcTitleSet().size());
          Thread.sleep((int) (Math.random() * 10000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class GetISOThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("ISO_Title: " + this.cache.getIso639_3_TitleSet().size());
          Thread.sleep((int) (Math.random() * 1000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class GetISOIdThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("ISO_Identifier: " + this.cache.getIso639_3_IdentifierSet().size());
          Thread.sleep((int) (Math.random() * 10000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class GetMIMEThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("MIME_Title: " + this.cache.getMimeTypesTitleSet().size());
          Thread.sleep((int) (Math.random() * 10000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class GetMPIPKSThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("MPIPKS_Title: " + this.cache.getMpipksTitleSet().size());
          Thread.sleep((int) (Math.random() * 10000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class GetMPIRKThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("MPIRK_Title: " + this.cache.getMpirgTitleSet().size());
          Thread.sleep((int) (Math.random() * 10000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class GetMPISGROUPThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("MPIS_GROUP_Title: " + this.cache.getMpisGroupsTitleSet().size());
          Thread.sleep((int) (Math.random() * 10000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class GetMPISPROJECTThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        try {
          System.out.println("MPIS_PROJECT_Title: " + this.cache.getMpisProjectTitleSet().size());
          Thread.sleep((int) (Math.random() * 10000));
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  class RefreshCacheThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        System.out.println("Start refreshCache");
        try {
          this.cache.refreshCache();
        } catch (final ValidationConeCacheConfigException e) {
          System.out.println(e);
        }
        System.out.println("Ende refreshCache");
        TestConeCache.this.logCacheSetSizes(this.cache);
        try {
          Thread.sleep(10000);
        } catch (final Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  private void logCacheSetSizes(ConeCache cache) {
    System.out.println("cDDC_Title: " + cache.getDdcTitleSet().size());
    System.out.println("cISO_Identifier: " + cache.getIso639_3_IdentifierSet().size());
    System.out.println("cISO_Title: " + cache.getIso639_3_TitleSet().size());
    System.out.println("cMIME_Title: " + cache.getMimeTypesTitleSet().size());
    System.out.println("cMPIPKS_Title: " + cache.getMpipksTitleSet().size());
    System.out.println("cMPIRK_Title: " + cache.getMpirgTitleSet().size());
    System.out.println("cMPIS_GROUP_Title: " + cache.getMpisGroupsTitleSet().size());
    System.out.println("cMPIS_PROJECT_Title: " + cache.getMpisProjectTitleSet().size());
  }

}
