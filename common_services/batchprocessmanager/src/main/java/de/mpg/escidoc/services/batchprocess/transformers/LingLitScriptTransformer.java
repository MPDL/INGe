package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
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
        item.getMetadata().getLanguages().addAll(list);
        // String locClass = getLocClassification(fkw);
        Map<String, String> languages = new HashMap<String, String>();
        logger.info("BEF trans: " + fkw);
        // if (fkw.contains("ISO 639-3"))
        // {
        // Pattern p = Pattern.compile("\\b[a-z][a-z][a-z]\\b");
        // Matcher m = p.matcher(fkw);
        // while (m.find())
        // {
        // String lang = getISOLanguage(m.group());
        // if (lang != null)
        // {
        // languages.put(m.group(), lang);
        // }
        // }
        // }
        String[] fields = fkw.split(",");
        if (fields.length > 0)
        {
            for (int i = 0; i < fields.length; i++)
            {
                if (fields[i].contains("ISO 639-3 : "))
                {
                    fields[i] = fields[i].replace("ISO 639-3 : ", "");
                    String[] langs = fields[i].split("/");
                    for (int j = 0; j < langs.length; j++)
                    {
                        item.getMetadata().getSubjects().add(new TextVO(langs[j], "eng", "eterms:ISO639_3"));
                        fkw = fkw.replace(langs[j], "");
                    }
                    fkw = fkw.replace("/", "");
                    fkw = fkw.replace("ISO 639-3 :,", "");
                }
                else if (i == 0 && fields[i].matches("\\b[A-Z0-9.]+\\b"))
                {
                    String locClass = fields[i];
                    fkw = fkw.replace(locClass, " LoC Class: " + locClass);
                }
                else
                {
                    fkw = fkw.replace(fields[i], " LoC Subject Heading: " + fields[i]);
                }
            }
        }
        else
        {
            logger.error("Error parsing freekeywords");
        }
        // Pattern p = Pattern.compile("\\b[\\w]+\\b");
        // Matcher m = p.matcher(fkw);
        // while (m.find())
        // {
        // languages.putAll(findLanguages(m.group()));
        // }
        // if (locClass != null)
        // {
        // fkw = fkw.replace(locClass, "LoC Class " + locClass);
        // }
        // if (languages.size() > 0)
        // {
        // for (String str : languages.keySet())
        // {
        // if (fkw.contains(str))
        // {
        // String language = languages.get(str);
        // fkw = fkw.replace(language, "LoC Subject Heading" + language);
        // }
        // }
        // }
        logger.info("AFT trans: " + fkw + " with languages");
        for (TextVO t : item.getMetadata().getSubjects())
        {
            if ("eterms:ISO639_3".equals(t.getType()))
            {
                logger.info(t.getValue());
            }
        }
        return item;
    }

    private String getLocClassification(String str)
    {
        Pattern p = Pattern.compile("\\b[A-Z0-9.]+\\b");
        Matcher m = p.matcher(str);
        if (m.find())
        {
            if (!"ISO".equals(m.group()))
            {
                return m.group();
            }
        }
        return null;
    }

    private Map<String, String> findLanguages(String str)
    {
        Map<String, String> map = new HashMap<String, String>();
        try
        {
            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod("http://dev-pubman.mpdl.mpg.de/cone/iso639-3/query?f=options&q=\""
                    + str + "\"");
            getMethod.setRequestHeader("Connection", "close");
            client.executeMethod(getMethod);
            String resp = getMethod.getResponseBodyAsString();
            String[] options = resp.split("[\\|\\n]");
            if (options.length > 1)
            {
                for (int i = 0; i < options.length; i++)
                {
                    if (options[i + 1].split("-").length > 1)
                    {
                        // logger.info("language: " + options[i + 1].split("-")[1].replace("\n", ""));
                        map.put(options[i], options[i + 1].split("-")[1].replace("\n", ""));
                        i++;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return map;
    }

    private String getISOLanguage(String str)
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod("http://dev-pubman.mpdl.mpg.de/cone/iso639-3/query?f=options&q=\""
                    + str + "\"");
            getMethod.setRequestHeader("Connection", "close");
            client.executeMethod(getMethod);
            String resp = getMethod.getResponseBodyAsString();
            if (resp.split("\\|").length > 1)
            {
                return resp.split("\\|")[1].replace("\n", "");
            }
            else
            {
                // logger.warn(str + " is not a language");
                // logger.warn("response: " + resp);
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
            if (lang.length() != 3)
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
        else if (currentIsoLangValue.equals("az"))
        {
            languages.set(i, "aze");
        }
        else if (currentIsoLangValue.equals("bg"))
        {
            languages.set(i, "bul");
        }
        else if (currentIsoLangValue.equals("bi"))
        {
            languages.set(i, "bis");
        }
        else if (currentIsoLangValue.equals("bn"))
        {
            languages.set(i, "ben");
        }
        else if (currentIsoLangValue.equals("bo"))
        {
            languages.set(i, "bod");
        }
        else if (currentIsoLangValue.equals("de"))
        {
            languages.set(i, "deu");
        }
        else if (currentIsoLangValue.equals("en"))
        {
            languages.set(i, "eng");
        }
        else if (currentIsoLangValue.equals("es"))
        {
            languages.set(i, "spa");
        }
        else if (currentIsoLangValue.equals("fr"))
        {
            languages.set(i, "fra");
        }
        else if (currentIsoLangValue.equals("gn"))
        {
            languages.set(i, "grn");
        }
        else if (currentIsoLangValue.equals("he"))
        {
            languages.set(i, "heb");
        }
        else if (currentIsoLangValue.equals("zh"))
        {
            languages.set(i, "zho");
        }
        else if (currentIsoLangValue.equals("hi"))
        {
            languages.set(i, "hin");
        }
        else if (currentIsoLangValue.equals("id"))
        {
            languages.set(i, "ind");
        }
        else if (currentIsoLangValue.equals("it"))
        {
            languages.set(i, "ita");
        }
        else if (currentIsoLangValue.equals("ka"))
        {
            languages.set(i, "kat");
        }
        else if (currentIsoLangValue.equals("kk"))
        {
            languages.set(i, "kaz");
        }
        else if (currentIsoLangValue.equals("kn"))
        {
            languages.set(i, "kan");
        }
        else if (currentIsoLangValue.equals("ko"))
        {
            languages.set(i, "kor");
        }
        else if (currentIsoLangValue.equals("la"))
        {
            languages.set(i, "lat");
        }
        else if (currentIsoLangValue.equals("mn"))
        {
            languages.set(i, "mon");
        }
        else if (currentIsoLangValue.equals("my"))
        {
            languages.set(i, "mya");
        }
        else if (currentIsoLangValue.equals("nl"))
        {
            languages.set(i, "nld");
        }
        else if (currentIsoLangValue.equals("pt"))
        {
            languages.set(i, "por");
        }
        else if (currentIsoLangValue.equals("qu"))
        {
            languages.set(i, "que");
        }
        else if (currentIsoLangValue.equals("ro"))
        {
            languages.set(i, "ron");
        }
        else if (currentIsoLangValue.equals("ru"))
        {
            languages.set(i, "rus");
        }
        else if (currentIsoLangValue.equals("tr"))
        {
            languages.set(i, "tur");
        }
        else
        {
            if (!"".equals(currentIsoLangValue))
                logger.error("NON KNOWN LANAGUAGE CODE: " + currentIsoLangValue);
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