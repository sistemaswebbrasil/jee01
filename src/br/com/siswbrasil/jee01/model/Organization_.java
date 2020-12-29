package br.com.siswbrasil.jee01.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-12-29T10:26:06.051-0200")
@StaticMetamodel(Organization.class)
public class Organization_ {
	public static volatile SingularAttribute<Organization, Long> id;
	public static volatile SingularAttribute<Organization, String> initials;
	public static volatile SingularAttribute<Organization, String> companyName;
	public static volatile SingularAttribute<Organization, String> tradingName;
}
