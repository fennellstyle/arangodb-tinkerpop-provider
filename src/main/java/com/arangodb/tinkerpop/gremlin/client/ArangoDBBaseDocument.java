//////////////////////////////////////////////////////////////////////////////////////////
//
// Implementation of a simple graph client for the ArangoDB.
//
// Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

package com.arangodb.tinkerpop.gremlin.client;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;
import com.arangodb.tinkerpop.gremlin.structure.ArangoDBGraphException;

/**
 * The ArangoDB document class provides the required ID, KEY and REV fields, and supports a
 * key-value map of properties. 
 * 
 * @author Achim Brandt (http://www.triagens.de)
 * @author Johannes Gocke (http://www.triagens.de)
 * @author Guido Schwab (http://www.triagens.de)
 * @author Jan Steemann (http://www.triagens.de)
 * @author Horacio Hoyos Rodriguez (@horaciohoyosr)
 */
@Deprecated
public abstract class ArangoDBBaseDocument {

	private static final Logger logger = LoggerFactory.getLogger(ArangoDBBaseDocument.class);

	
	
	/**
	 * true if the document is deleted
	 */
	protected boolean deleted = false;
	
	/**
	 * If an id (KEY) is not supplied
	 * @param properties
	 */
	public ArangoDBBaseDocument(Map<String, Object> properties) {
		super();
		this.properties = new TreeMap<>(properties);
	}
	
	/**
	 * 
	 * @param id
	 * @param properties
	 */
	public ArangoDBBaseDocument(String id, Map<String, Object> properties) {
		super();
		this.id = id;
		this.properties = new TreeMap<>(properties);
	}


	/**
	 * Sets the document status to "deleted"
	 */
	public void setDeleted() {
		logger.info("setDeleted");
		properties.clear();
		deleted = true;
	}

	/**
	 * Returns true if the document has status "deleted"
	 * 
	 * @return true, if the document is deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}
	

	protected void checkHasProperty(String name) throws ArangoDBGraphException {
		if (!properties.containsKey(name)) {
			throw new ArangoDBGraphException("Missing property '" + name + "'");
		}
	}

	/**
	 * Returns a property value
	 * 
	 * @param key
	 *            The key of the property
	 * 
	 * @return the property value
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * Returns property keys
	 * 
	 * @return the property keys
	 */
	public Set<String> getPropertyKeys() {
		return properties.keySet();
	}

	/**
	 * Set a single property value
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 * 
	 * @throws ArangoDBGraphException
	 *             if an error occurs
	 */
	public void setProperty(String key, Object value) throws ArangoDBGraphException {
		if (StringUtils.isNotBlank(key)) {
			if (key.charAt(0) != '_' && key.charAt(0) != '$') {
				properties.put(key, value);
			} else {
				throw new ArangoDBGraphException("Property name is reserved (i.e. property names can not start with "
						+ "underscore (_) or money sign ($)");
			}
		} else {
			throw new ArangoDBGraphException("Property name cannot be empty");
		}
	}


	/**
	 * Removes a single property
	 * 
	 * @param key
	 *            the key of the property
	 * 
	 * @return the value of the removed property
	 * 
	 * @throws ArangoDBGraphException
	 *             if an error occurs
	 */
	public Object removeProperty(String key) throws ArangoDBGraphException {
		try {
			return properties.remove(key);
		} catch (ClassCastException | NullPointerException ex) {
			throw new ArangoDBGraphException("Error removing property.", ex);
		}
	}

	protected String getStringProperty(String key) {
		return this.properties.getOrDefault(key, "").toString();
	}

	/**
	 * Returns the document identifier
	 * 
	 * @return the document identifier
	 */
	public String getDocumentId() {
		return arango_id;
	}

	/**
	 * Returns the document version
	 * 
	 * @return the document version
	 */
	public String getDocumentRev() {
		return arango_rev;
	}

	/**
	 * Returns the document long identifier
	 * 
	 * @return the document long identifier
	 */
	public String getDocumentKey() {
		return id;
	}

	@Override
	public String toString() {
		if (properties == null) {
			return "null";
		}
		return properties.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arango_id == null) ? 0 : arango_id.hashCode());
		result = prime * result + ((arango_rev == null) ? 0 : arango_rev.hashCode());
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArangoDBBaseDocument other = (ArangoDBBaseDocument) obj;
		if (arango_id == null) {
			if (other.arango_id != null)
				return false;
		} else if (!arango_id.equals(other.arango_id))
			return false;
		if (arango_rev == null) {
			if (other.arango_rev != null)
				return false;
		} else if (!arango_rev.equals(other.arango_rev))
			return false;
		if (deleted != other.deleted)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}

}
