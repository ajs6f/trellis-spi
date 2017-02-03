/*
 * Copyright Amherst College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.amherst.acdc.trellis.spi;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.amherst.acdc.trellis.vocabulary.ACL;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;

/**
 * This class provides access to data defined in an WebAC Authorization graph. Access to a resource can be
 * controlled via WebAccessControl, an RDF-based access control system. A resource can define an ACL resource
 * via the Link header, using rel=acl. It can also point to an ACL resource using a triple in the resource's own
 * RDF graph via acl:accessControl. Absent an acl:accessControl triple, the parent resource is checked, up to the
 * server's root resource.
 *
 * An ACL resource may contain multiple acl:Authorization sections. In an LDP context, this may be represented with
 * ldp:contains triples. Another common pattern is to refer to the acl:Authorization sections with blank nodes.
 *
 * @see <a href="https://www.w3.org/wiki/WebAccessControl">W3C WebAccessControl</a>
 * and <a href="https://github.com/solid/web-access-control-spec">Solid WebAC specification</a>
 *
 * @author acoburn
 */
public class Authorization {

    private final IRI identifier;
    private final Map<IRI, Set<IRI>> dataMap = new HashMap<IRI, Set<IRI>>() { {
        put(ACL.agent, new HashSet<>());
        put(ACL.agentClass, new HashSet<>());
        put(ACL.agentGroup, new HashSet<>());
        put(ACL.mode, new HashSet<>());
        put(ACL.accessTo, new HashSet<>());
        put(ACL.accessToClass, new HashSet<>());
        put(ACL.defaultForNew, new HashSet<>());
    }};

    /**
     * Create an Authorization object from a graph and an
     * identifier
     * @param identifier the identifier
     * @param graph the graph
     * @return the Authorization object
     */
    public static Authorization of(final IRI identifier, final Graph graph) {
        return new Authorization(identifier, graph);
    }

    /**
     * Create an Authorization object from an RDF graph
     * @param identifier the subject IRI
     * @param graph the RDF graph
     */
    public Authorization(final IRI identifier, final Graph graph) {
        requireNonNull(identifier, "The Authorization identifier may not be null!");
        requireNonNull(graph, "The input graph may not be null!");

        this.identifier = identifier;
        graph.stream(identifier, null, null).forEach(triple -> {
            if (dataMap.containsKey(triple.getPredicate()) && triple.getObject() instanceof IRI) {
                dataMap.get(triple.getPredicate()).add((IRI) triple.getObject());
            }
        });
    }

    /**
     * Retrieve the identifier for this Authorization
     * @return the identifier
     */
    public IRI getIdentifier() {
        return identifier;
    }

    /**
     * Retrieve the agents that are associated with this Authorization
     * @return the Agent values
     */
    public Set<IRI> getAgent() {
        return unmodifiableSet(dataMap.get(ACL.agent));
    }

    /**
     * Retrieve the agent classes that are associated with this Authorization
     * @return the Agent class values
     */
    public Set<IRI> getAgentClass() {
        return unmodifiableSet(dataMap.get(ACL.agentClass));
    }

    /**
     * Retrieve the agent groups that are associated with this Authorization
     * @return the Agent groups values
     */
    public Set<IRI> getAgentGroup() {
        return unmodifiableSet(dataMap.get(ACL.agentGroup));
    }

    /**
     * Retrieve the access modes that are associated with this Authorization
     * @return the access mode values
     */
    public Set<IRI> getMode() {
        return unmodifiableSet(dataMap.get(ACL.mode));
    }

    /**
     * Retrieve the resource identifiers to which this Authorization applies
     * @return the accessTo values
     */
    public Set<IRI> getAccessTo() {
        return unmodifiableSet(dataMap.get(ACL.accessTo));
    }


    /**
     * Retrieve the accessToClass values that are associated with this Authorization
     * @return the accessToClass values
     */
    public Set<IRI> getAccessToClass() {
        return unmodifiableSet(dataMap.get(ACL.accessToClass));
    }

    /**
     * Retrieve the directories for which this authorization is used for new resources in the container
     * @return the resource identifiers
     */
    public Set<IRI> getDefaultForNew() {
        return unmodifiableSet(dataMap.get(ACL.defaultForNew));
    }
}
