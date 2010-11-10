package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.framework.ServiceLocator;

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
            item = setIsoToLanguages(item);
            report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                    + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
        }
        return list;
    }

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
        List<String> list = new ArrayList<String>();
        for (String str : item.getMetadata().getLanguages())
        {
            System.out.println(str);
            list.add("eng");
        }
        item.getMetadata().getLanguages().addAll(list);
        if (fkw.contains("ISO 639-3"))
        {
            // logger.info(fkw);
            Pattern p = Pattern.compile("\\b[a-z][a-z][a-z]\\b");
            Matcher m = p.matcher(fkw);
            while (m.find())
            {
                String lang = getISOLanguage(m.group());
                if (lang != null)
                {
                    String code = m.group();
                    logger.info("Language : " + lang + " with code : " + code);
                }
            }
        }
        return item;
    }

    private String getISOLanguage(String str)
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod("http://pubman.mpdl.mpg.de/cone/iso639-3/query?q=\"" + str + "\"");
            getMethod.setRequestHeader("Accept", "text/plain");
            client.executeMethod(getMethod);
            String resp = getMethod.getResponseBodyAsString();
            if (resp.split("\\|").length > 1)
            {
                return resp.split("\\|")[1];
            }
            else
            {
                logger.warn(str + " is not a language");
                logger.warn("response: " + resp);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    public PubItemVO setIsoToLanguages(PubItemVO item)
    {
        List<String> languages = item.getMetadata().getLanguages();
        for (int i = 0; i < languages.size(); i++)
        {
            String currentIsoLangValue = languages.get(i);
            String lang = currentIsoLangValue;
            int l = lang.length();
            if (l <= 2)
            {
                setIso639_3(languages, i, currentIsoLangValue);
            }
        }
        return item;
    }

    private void setIso639_3(List<String> languages, int i, String currentIsoLangValue)
    {
        if (currentIsoLangValue.equals("ar"))
        {
            languages.set(i, "ara");
        }
        if (currentIsoLangValue.equals("az"))
        {
            languages.set(i, "aze");
        }
        if (currentIsoLangValue.equals("bg"))
        {
            languages.set(i, "bul");
        }
        if (currentIsoLangValue.equals("bi"))
        {
            languages.set(i, "bis");
        }
        if (currentIsoLangValue.equals("bn"))
        {
            languages.set(i, "ben");
        }
        if (currentIsoLangValue.equals("bo"))
        {
            languages.set(i, "bod");
        }
        if (currentIsoLangValue.equals("de"))
        {
            languages.set(i, "deu");
        }
        if (currentIsoLangValue.equals("en"))
        {
            languages.set(i, "eng");
        }
        if (currentIsoLangValue.equals("es"))
        {
            languages.set(i, "spa");
        }
        if (currentIsoLangValue.equals("fr"))
        {
            languages.set(i, "fra");
        }
        if (currentIsoLangValue.equals("gn"))
        {
            languages.set(i, "grn");
        }
        if (currentIsoLangValue.equals("he"))
        {
            languages.set(i, "heb");
        }
        if (currentIsoLangValue.equals("hi"))
        {
            languages.set(i, "hin");
        }
        if (currentIsoLangValue.equals("id"))
        {
            languages.set(i, "ind");
        }
        if (currentIsoLangValue.equals("it"))
        {
            languages.set(i, "ita");
        }
        if (currentIsoLangValue.equals("ka"))
        {
            languages.set(i, "kat");
        }
        if (currentIsoLangValue.equals("kk"))
        {
            languages.set(i, "kaz");
        }
        if (currentIsoLangValue.equals("kn"))
        {
            languages.set(i, "kan");
        }
        if (currentIsoLangValue.equals("ko"))
        {
            languages.set(i, "kor");
        }
        if (currentIsoLangValue.equals("la"))
        {
            languages.set(i, "lat");
        }
        if (currentIsoLangValue.equals("mn"))
        {
            languages.set(i, "mon");
        }
        if (currentIsoLangValue.equals("my"))
        {
            languages.set(i, "mya");
        }
        if (currentIsoLangValue.equals("nl"))
        {
            languages.set(i, "nld");
        }
        if (currentIsoLangValue.equals("pt"))
        {
            languages.set(i, "por");
        }
        if (currentIsoLangValue.equals("qu"))
        {
            languages.set(i, "que");
        }
        if (currentIsoLangValue.equals("ro"))
        {
            languages.set(i, "ron");
        }
        if (currentIsoLangValue.equals("ru"))
        {
            languages.set(i, "rus");
        }
        if (currentIsoLangValue.equals("tr"))
        {
            languages.set(i, "tur");
        }
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

    private PubItemVO transformLocators(PubItemVO item)
    {
        for (FileVO f : item.getFiles())
        {
            if (Storage.EXTERNAL_URL.equals(f.getStorage()))
            {
                f.setName(f.getContent());
            }
        }
        return item;
    }

    private void processingAlternativeTitles(PubItemVO item, String title)
    {
        for (SourceVO source : item.getMetadata().getSources())
        {
            for (int i = 0; i < source.getAlternativeTitles().size(); i++)
            {
                String subtitle = source.getAlternativeTitles().get(i).getValue();
                if (!(subtitle.equals("") || subtitle == null))
                {
                    if (subtitle.equals(title))
                    {
                        source.getAlternativeTitles().remove(i);
                    }
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