/**
 *
 */
package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * Configuration of a {@link Configurable} workflow bean.
 * <p>
 * A configuration is activated by a {@link Profile}, and provides a link to the
 * {@link #getJson()} containing the properties to configure the
 * bean, like an {@link Activity}.
 * <p>
 * A configuration is of a certain (RDF) <strong>type</strong>, as defined by
 * {@link #getType()} - which determines which
 * properties are required and optional. For instance, the type
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/ConfigType</code> requires
 * the property
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/operation</code>.
 * <p>
 * These requirements are described in the {@link ConfigurationDefinition} that
 * matches the getConfigurableType() of the {@link Configurable} found by
 * {@link #getConfigures()}. Its
 * {@link ConfigurationDefinition#getPropertyResourceDefinition()} should in
 * {@link PropertyResourceDefinition#getTypeURI()} should match the
 * {@link #getType()} of this {@link Configuration}.
 * <p>
 * Note: {@link #getType()} (and the potentially misleading
 * {@link #getType()}) return the type of <b>this</b> Configuration,
 * not the type of the {@link Configurable} bean that it happens to configure.
 * For instance, a Configuration typed
 * <code>http://example.com/WSDLConfiguration</code> might configure an activity
 * typed <code>http://example.com/WSDLActivity</code>, but could also have
 * configured an activity typed
 * <code>http://example.com/GlobusWSDLActivity</code>.
 * <p>
 * <strong>TODO: Where are the ConfigurationDefinitions found?</strong>
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * 
 */
public class Configuration extends AbstractNamed implements Child<Profile>, Typed {
	private static final JsonNodeFactory JSON_NODE_FACTORY = new JsonNodeFactory(true);
    private Configurable configures;
	private Profile parent;
	private JsonNode json = JSON_NODE_FACTORY.objectNode();
	private JsonSchema jsonSchema;
    private URI type;

	/**
	 * Constructs a <code>Configuration</code> with a random UUID as the name.
	 */
	public Configuration() {
		super();
	}

	/**
	 * Construct a <code>Configuration</code> with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Configuration</code>. <strong>Must not</strong> be
	 *            <code>null</code> or an empty String.
	 */
	public Configuration(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
	    return visitor.visit(this);
	}

	/**
	 * Return the {@link Configurable} workflow bean that is configured. Typically an
	 * {@link Activity} or {@link DispatchStackLayer}, but in theory also {@link Processor},
	 * {@link Workflow} and {@link Port} can be configured.
	 * 
	 * @return the <code>Configurable</code> <code>WorkflowBean</code> that is configured
	 */
	public Configurable getConfigures() {
		return configures;
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	/**
	 * Return the underlying {@link PropertyResource} which contains the properties set by this
	 * configuration.
	 * 
	 * @return the backing {@link PropertyResource}.
	 */
	public JsonNode getJson() {
		return json;
	}

	/**
	 * Return the type of the <code>Configuration</code>.
	 * <p>
	 * The URI will match the {@link PropertyResource#getTypeURI()}.
	 * 
	 * @return the type of the <code>Configuration</code>
	 */
	public URI getType() {
		return type;
	}

	/**
	 * Set the {@link Configurable} {@link WorkflowBean} that is configured.
	 * 
	 * @param configurable
	 *            the <code>Configurable</code> <code>WorkflowBean</code> that is configured
	 */
	public void setConfigures(Configurable configurable) {
		configures = configurable;
	}

	@Override
	public void setParent(Profile parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getConfigurations().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getConfigurations().add(this);
		}

	}

	/**
	 * Set the underlying {@link PropertyResource} which contains the properties
	 * set by this configuration.
	 * <p>
	 * If the provided PropertyResource is <code>null</code>, a new, blank
	 * PropertyResource will be set instead.
	 * 
	 * @param json
	 *            the underlying <code>PropertyResource</code> which contains
	 *            the properties set by this configuration.
	 */
	public void setJson(JsonNode json) {
		if (json == null) {
		    // TODO: Should this be JSON_NODE_FACTORY.nullNode();
			this.json = JSON_NODE_FACTORY.objectNode();
		}
		this.json = json;
	}

	/**
	 * Set the type of the <code>Configuration</code>.
	 * <p>
	 * This will also set {@link PropertyResource#setTypeURI(URI)}.
	 * 
	 * @param type
	 *             the type of the <code>Configuration</code>.
	 */
	public void setType(URI type) {
	    this.type = type;
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		Configuration cloneConfig = (Configuration) clone;
		cloneConfig.setConfigures(cloning.cloneOrOriginal(getConfigures()));
	}

    public JsonSchema getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(JsonSchema jsonSchema) {
        this.jsonSchema = jsonSchema;
    }
	
}
