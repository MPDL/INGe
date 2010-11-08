package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;

public class LingLitScriptTransformer extends Transformer<PubItemVO>
{
    @Override
    public List<PubItemVO> transform(List<PubItemVO> list)
    {
        for (PubItemVO item : list)
        {
            item = removeURI(item);
            item = transformAlternativeTitle(item);
            item = transformDegree(item);
            report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                    + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
            // Languages START
            System.out.println("============= Languages START ===============");
            for (String language : item.getMetadata().getLanguages())
            {
                System.out.println(language);
            }
            System.out.println("============== Languages END ================");
            // Languages END
            // TITLE START
            System.out.println("============= TITLE START ===============");
            for (SourceVO titles : item.getMetadata().getSources())
            {
                System.out.println(titles.getTitle().getValue());
            }
            System.out.println("============== TITLE END ================");
            // TITLE END
            // SUBTITLE START
            System.out.println("============= SUBTITLE START ===============");
            for (SourceVO source : item.getMetadata().getSources())
            {
                for (TextVO alt : source.getAlternativeTitles())
                {
                    System.out.println(alt.getValue());
                }
            }
            System.out.println("============== SUBTITLE END ================");
            // SUBTITLE END
            // KEYWORDS START
            System.out.println("============= KEYWORDS START ===============");
            String keywords = item.getMetadata().getFreeKeywords().getValue();
            System.out.println(keywords);
            // for (TextVO subjects : item.getMetadata().getSubjects())
            // {
            // System.out.println("KEY WORDS: >>>>>>>>>>>" +
            // subjects.getValue());
            // }
            System.out.println("============== KEYWORDS END ================");
            // KEYWORDS END
            // DEGREE START
            System.out.println("============= DEGREE START ===============");
            DegreeType degree = item.getMetadata().getDegree();
            System.out.println(degree);
            System.out.println("============== DEGREE END ================");
            // DEGREE END
            // URL START
            System.out.println("============= URL START ===============");
            // List<IdentifierVO> uris = item.getMetadata().getIdentifiers();
            for (IdentifierVO uris : item.getMetadata().getIdentifiers())
            {
                System.out.println("Identifier: " + uris.getId() + " ,type: " + uris.getTypeString());
            }
            System.out.println("============== URL END ================");
            // URL END
        }
        return list;
    }
    
    // Test with escidoc:173940
    public PubItemVO transformAlternativeTitle(PubItemVO item)
    {
        for (SourceVO src : item.getMetadata().getSources())
        {
            for (TextVO alt : src.getAlternativeTitles())
            {
                if (alt.getValue().equals("Serie Lingüística peruana"))
                {
                    System.out.println("MATCHED");
                }
                else
                {
                    System.out.println("NOT MATCHED");
                }
            }
        }
        return item;
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