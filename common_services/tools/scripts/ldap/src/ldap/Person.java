package ldap;

public class Person{
	private String commonName;
	private String uid;
	private String mail;
	private String password;
	private String givenname;
	private String surname;
	
	public Person () {
		
	}
	
	public Person (String userCommonName, String userUid, String userMail, String userPassword, String userGivenname, String userSurname) {
		this.commonName = userCommonName;
		this.uid = userUid;
		this.mail = userMail;
		this.password = userPassword;
		this.givenname = userGivenname;
		this.surname = userSurname;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGivenname() {
		return givenname;
	}

	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String toXmlString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n\t<person>");
        sb.append("\n\t\t<cn>" + this.commonName + "</cn>");
        sb.append("\n\t\t<uid>" + this.uid + "</uid>");
        sb.append("\n\t\t<mail>" + this.mail + "</mail>");
        sb.append("\n\t\t<password>" + this.password + "</password>");
        sb.append("\n\t\t<givenname>" + this.givenname + "</givenname>");
        sb.append("\n\t\t<surname>" + this.surname + "</surname>");
        sb.append("\n\t</person>");
        return sb.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n----------------------------");
        sb.append("\n Person Common Name = " + this.commonName);
        sb.append("\n Person UID = " + this.uid);
        sb.append("\n Person Mail = " + this.mail);
        sb.append("\n Person Password = " + this.password);
        sb.append("\n Person Givenname = " + this.givenname);
        sb.append("\n Person Surname = " + this.surname);
        sb.append("\n----------------------------");
        return sb.toString();
	}
}
