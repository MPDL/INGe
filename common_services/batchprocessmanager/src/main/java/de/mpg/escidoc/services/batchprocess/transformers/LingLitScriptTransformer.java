package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.framework.AdminHelper;

public class LingLitScriptTransformer extends Transformer<PubItemVO>
{
    private static Logger logger = Logger.getLogger(LingLitScriptTransformer.class);

    @Override
    public List<PubItemVO> transform(List<PubItemVO> list)
    {
        System.out.println("Number of items: " + list.size());
        for (PubItemVO item : list)
        {
            item = removeURI(item);
            item = transformAlternativeTitle(item);
            item = transformDegree(item);
            item = assignUserGroup(item);
            item = transformLocators(item);
            item = transformFreeKeyWords(item);
            report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                    + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
        }
        return list;
    }

    // Test with escidoc:173940
    public PubItemVO transformAlternativeTitle(PubItemVO item)
    {
        for (SourceVO titles : item.getMetadata().getSources())
        {
            String title = titles.getTitle().getValue();
            if (!(title.equals("") || (title == null)))
            {
                if (title.indexOf(";") > 0)
                {
                    title = title.substring(0, titles.getTitle().getValue().indexOf(";") - 1);
                    processingAlternativeTitles(item, title);
                }
                else
                {
                    processingAlternativeTitles(item, title);
                }
            }
        }
        return item;
    }

    public PubItemVO transformFreeKeyWords(PubItemVO item)
    {
        String fkw = item.getMetadata().getFreeKeywords().getValue();
        // logger.info("free keywords : " + fkw);
        if (fkw.contains("ISO 639-3"))
        {
            logger.info(fkw);
            Pattern p = Pattern.compile("[a-z][a-z][a-z]\\W");
            Matcher m = p.matcher(fkw);
            while (m.find())
            {
                logger.info("Possible Language : " + m.group());
            }
        }
        return item;
    }

    public PubItemVO assignUserGroup(PubItemVO item)
    {
        for (FileVO f : item.getFiles())
        {
            if (Visibility.AUDIENCE.equals(f.getVisibility()))
            {
                Grant grant = new Grant();
                grant.setAssignedOn(f.getReference().getObjectId());
                grant.setGrantedTo("escidoc:175089");
                grant.setGrantType("user-group");
                grant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
                try
                {
                    // SHOULD STAY OUT COMMENTED UNTIL ACTUAL EDIT
                    // grant
                    // .createInCoreservice(AdminHelper.loginUser("bibliothek_mpi_eva", "bibliothek"),
                    // "Edit Linglit");
                }
                catch (Exception e)
                {
                    logger.warn(f.getReference().getObjectId() + " has already escidoc:175089 as user group");
                }
            }
        }
        return item;
    }

    public PubItemVO transformLocators(PubItemVO item)
    {
        for (FileVO f : item.getFiles())
        {
            if (Storage.EXTERNAL_URL.equals(f.getStorage()))
            {
                if (!f.getName().equals(f.getContent()))
                {
                    // logger.info("Item " + item.getVersion().getObjectId() + " has " + f.getName() +
                    // " is different to "
                    // + f.getContent());
                }
            }
        }
        return item;
    }

    private void processingAlternativeTitles(PubItemVO item, String title)
    {
        for (SourceVO source : item.getMetadata().getSources())
        {
            for (TextVO alternative : source.getAlternativeTitles())
            {
                String subtitle = alternative.getValue();
                if (!(subtitle.equals("") || subtitle == null))
                {
                    if (subtitle.equals(title))
                    {
                        source.getAlternativeTitles().remove(this);
                        System.out.println("==============================================");
                        System.out.println("TITLE: " + title);
                        System.out.println("ALTERNATIVE TITLE: " + subtitle);
                        System.out.println("MATCHED");
                        System.out.println("==============================================");
                    }
                }
                else
                {
                    System.out.println("==============================================");
                    System.out.println("TITLE: " + title);
                    System.out.println("ALTERNATIVE TITLE: " + subtitle);
                    System.out.println("NOT MATCHED");
                    System.out.println("==============================================");
                }
            }
        }
    }

    public PubItemVO transformDegree(PubItemVO item)
    {
        if (DegreeType.MAGISTER.equals(item.getMetadata().getDegree()))
        {
            item.getMetadata().setDegree(DegreeType.MASTER);
        }
        return item;
    }

    public PubItemVO removeURI(PubItemVO item)
    {
        for (int i = 0; i < item.getMetadata().getIdentifiers().size(); i++)
        {
            if (IdType.URI.equals(item.getMetadata().getIdentifiers().get(i).getType()))
            {
                item.getMetadata().getIdentifiers().remove(i);
            }
        }
        return item;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}