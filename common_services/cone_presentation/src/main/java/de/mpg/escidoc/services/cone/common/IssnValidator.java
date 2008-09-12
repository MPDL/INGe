package de.mpg.escidoc.services.cone.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import de.mpg.escidoc.services.cone.journalnames.JournalDBAO;

public class IssnValidator implements Validator
{
    private static final String ISSN_REGEX = "[0-9]{4}[-][0-9]{3}[0-9|X]";
    private JournalDBAO jdao;

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
    {
        Pattern pat = null;
        pat = Pattern.compile(ISSN_REGEX);
        String val = (String)value;
        Matcher mat = pat.matcher(val);
        if (!mat.matches())
        {
            FacesMessage msg = new FacesMessage();
            msg.setDetail("invalid ISSN");
            msg.setSummary("check ISSN format");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
        else
        {
            boolean exists = false;
            jdao = new JournalDBAO();
            exists = jdao.checkExistingJournalNameAR(val);
            if (exists)
            {
                FacesMessage msg = new FacesMessage();
                msg.setDetail("Journal with ISSN "+val+" already exists!");
                msg.setSummary("check ISSN for new Journal name");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }
}
