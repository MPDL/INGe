package de.mpg.mpdl.inge.migration.beans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Migration {

  static Logger log = Logger.getLogger(Migration.class.getName());

  @Value("${escidoc.url}")
  private String escidocUrl;

  @Autowired
  private Reindexing reIndexing;
  @Autowired
  private ContextImportBean ctxImport;
  @Autowired
  private OrganizationImportBean ouImport;
  @Autowired
  private UserImportBean userImport;
  @Autowired
  private ItemImportBean itemImport;
  @Autowired
  private YearBookImportBean ybImport;
  @Autowired
  private MigrationUtilBean util;

  public void run(String what, String id) throws Exception {
    switch (what) {
      case "ctxs":
        ctxImport.importContexts();
        break;
      case "ctxs_reindex":
        reIndexing.reindexContexts();
        break;
      case "ous":
        ouImport.importAffs();
        break;
      case "ous_reindex":
        reIndexing.reindexOus();
        break;
      case "items":
        itemImport.importPubItems();
        break;
      case "items_reindex":
        System.out.println("calling reindexitems");
        reIndexing.reindexItems();
        break;
      case "single":
        if (id != null) {
          itemImport.importSinglePubItem(id);
          // reIndexing.reindexItem(id);
        }
        // util.wfTesting();
        break;
      case "single_reindex":
        if (id != null) {
          reIndexing.reindexItem(id);
        }
        break;
      case "list":
        if (id != null) {
          Path list = Paths.get(id);
          try {
            Files.lines(list).parallel().forEach(line -> itemImport.importSinglePubItem(line));
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        break;
      case "users":
        userImport.importUsers();
        break;
      case "single_user":
        if (id != null) {
          userImport.importSingleUser(id);
          reIndexing.reindexUser(id.replace("escidoc:", "user_"));
        }
        break;
      case "users_reindex":
        reIndexing.reindexUsers();
        break;
      case "logins":
        userImport.importLogins();
        break;
      case "all":
        ouImport.importAffs();
        ctxImport.importContexts();
        userImport.importUsers();
        userImport.importLogins();
        itemImport.importPubItems();;
        break;
      case "yb":
        ybImport.importYearBooks();
        reIndexing.reindexYBs();
        break;
      case "single_yb":
        if (id != null) {
          ybImport.importSingleYearbook(id);
          reIndexing.reindexYB(id.replace("escidoc:", "yb_"));
        }
        break;
      case "wwf":
        util.wfTesting();
        break;
      default:
        log.info("you don't really know, what exactly you want to do!!!");
    }
  }



}
