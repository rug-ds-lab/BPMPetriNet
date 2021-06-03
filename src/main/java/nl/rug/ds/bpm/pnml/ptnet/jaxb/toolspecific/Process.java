package nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process.Group;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process.Role;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.process.Variable;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "process")
public class Process {
	private Set<Variable> variables;
	private Set<Group> groups;
	private Set<Role> roles;

	public Process() {
		variables = new HashSet<>();
		groups = new HashSet<>();
		roles = new HashSet<>();
	}

	@XmlElementWrapper(name = "variables")
	@XmlElement(name = "variable")
	public Set<Variable> getVariables() {
		return variables;
	}

	public void setVariables(Set<Variable> variables) {
		this.variables = variables;
	}

	@XmlElementWrapper(name = "groups")
	@XmlElement(name = "group")
	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@XmlElementWrapper(name = "roles")
	@XmlElement(name = "role")
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
