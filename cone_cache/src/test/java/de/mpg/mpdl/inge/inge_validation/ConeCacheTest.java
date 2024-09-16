package de.mpg.mpdl.inge.inge_validation;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.cone_cache.ConeCache;

public class ConeCacheTest {

  public static void main(String[] args) {
    System.out.println("Start");

    final ConeCacheTest t = new ConeCacheTest();
    t.go();

    System.out.println("Ende");
  }

  public void go() {
    final List<Thread> list = new ArrayList<Thread>();
    list.add(new GetDDLThread());
    list.add(new GetISOThread());
    list.add(new GetISOIdThread());
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

  class RefreshCacheThread extends Thread {
    ConeCache cache = ConeCache.getInstance();

    @Override
    public void run() {
      while (true) {
        System.out.println("Start refreshCache");
        try {
          ConeCache.refreshCache();
        } catch (final Exception e) {
          System.out.println(e);
        }
        System.out.println("Ende refreshCache");
        ConeCacheTest.this.logCacheSetSizes(this.cache);
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
  }
}
