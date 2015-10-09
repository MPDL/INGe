package ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


public class Ldap {
	
	private DirContext ctx;
	
	public Ldap(String serverUrl, String baseDn, String userName, String userPassword) {
		//Connection setup
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, serverUrl + baseDn);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, userPassword);
        
        try {
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
        	System.out.println("An error occoured, while initializing the context [Ldap()]");
            e.printStackTrace();
        } 
	}
	
	@PreDestroy
	public void closeContext() {
		if(ctx != null)
		{
			try {
				ctx.close();
			} catch (NamingException e) {
				System.out.println("An error occoured, while closing the context [closeContext()]");
				e.printStackTrace();
			}
		}
	}
	
	public List <Person> getUserList(){
		
		NamingEnumeration<?> results = null;
		List <Person> personList = new ArrayList<Person>();
		try {
			SearchControls controls = new SearchControls();
	        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        results = ctx.search("", "(objectclass=person)", controls);
	        
	        while (results.hasMore()) {
	            SearchResult searchResult = (SearchResult) results.next();
	            Attributes attributes = searchResult.getAttributes();
	            Person person = new Person(
	            		attributes.get("cn").get().toString(),
	            		attributes.get("uid").get().toString(),
	            		attributes.get("mail") != null ? attributes.get("mail").get().toString() : "",
	            		attributes.get("userPassword") != null ? attributes.get("userPassword").get().toString() : Util.randomPasword(),
	            		attributes.get("givenName") != null ? attributes.get("givenName").get().toString() : "",
	            		attributes.get("sn") != null ? attributes.get("sn").get().toString() : ""
	            );
	           personList.add(person);
	           System.out.println(person.toString());
	        }
		} catch (NamingException e) {
			System.out.println("An error occoured, while getting LDAP-User-List [getUserList()]");
			e.printStackTrace();
		} finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                }
            }
		}
		return personList;
		
	}
	
	
	
}
