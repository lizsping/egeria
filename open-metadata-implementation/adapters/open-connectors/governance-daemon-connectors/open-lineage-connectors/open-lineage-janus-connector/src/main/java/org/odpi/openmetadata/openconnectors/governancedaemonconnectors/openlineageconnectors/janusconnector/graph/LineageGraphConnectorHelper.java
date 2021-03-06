/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.graph;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.odpi.openmetadata.governanceservers.openlineage.model.LineageEdge;
import org.odpi.openmetadata.governanceservers.openlineage.model.LineageVertex;
import org.odpi.openmetadata.governanceservers.openlineage.model.LineageVerticesAndEdges;
import org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.bothE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.hasLabel;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.inE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.outE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.until;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.ASSET_SCHEMA_TYPE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.ATTRIBUTE_FOR_SCHEMA;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.CONNECTION;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.DATABASE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.DATA_FILE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.DEPLOYED_DB_SCHEMA_TYPE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.FILE_FOLDER;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.FOLDER_HIERARCHY;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.GLOSSARY_TERM;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.NESTED_SCHEMA_ATTRIBUTE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.RELATIONAL_COLUMN;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.RELATIONAL_DB_SCHEMA_TYPE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.RELATIONAL_TABLE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.TABULAR_COLUMN;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.Constants.TABULAR_SCHEMA_TYPE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.CONDENSED_NODE_DISPLAY_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.DESTINATION_CONDENSATION;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_ANTONYM;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_CLASSIFICATION;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_COLUMN_DATA_FLOW;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_CONDENSED;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_IS_A_RELATIONSHIP;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_RELATED_TERM;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_REPLACEMENT_TERM;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_SEMANTIC_ASSIGNMENT;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_SYNONYM;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_TABLE_DATA_FLOW;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_TERM_CATEGORIZATION;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.EDGE_LABEL_TRANSLATION;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.NODE_LABEL_CONDENSED;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.NODE_LABEL_SUB_PROCESS;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_CONNECTION_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_DATABASE_DISPLAY_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_DISPLAY_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_ENTITY_GUID;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_LABEL;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_PATH;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_PREFIX_ELEMENT;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_PREFIX_VERTEX_INSTANCE_PROPERTY;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_PROCESS_GUID;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_SCHEMA_DISPLAY_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_SCHEMA_TYPE_DISPLAY_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_KEY_TABLE_DISPLAY_NAME;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_VALUE_NODE_ID_CONDENSED_DESTINATION;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.PROPERTY_VALUE_NODE_ID_CONDENSED_SOURCE;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.SOURCE_CONDENSATION;
import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.openlineageconnectors.janusconnector.utils.GraphConstants.immutableReturnedPropertiesWhiteList;

public class LineageGraphConnectorHelper {

    private GraphTraversalSource g;
    private String[] glossaryTermAndClassificationEdges = {EDGE_LABEL_SEMANTIC_ASSIGNMENT, EDGE_LABEL_RELATED_TERM,
            EDGE_LABEL_SYNONYM, EDGE_LABEL_ANTONYM, EDGE_LABEL_REPLACEMENT_TERM, EDGE_LABEL_TRANSLATION, EDGE_LABEL_IS_A_RELATIONSHIP,
            EDGE_LABEL_CLASSIFICATION, EDGE_LABEL_TERM_CATEGORIZATION};
    private String[] relationalColumnAndClassificationEdges = {NESTED_SCHEMA_ATTRIBUTE, EDGE_LABEL_CLASSIFICATION, EDGE_LABEL_SEMANTIC_ASSIGNMENT};
    private String[] tabularColumnAndClassificationEdges = {ATTRIBUTE_FOR_SCHEMA, EDGE_LABEL_CLASSIFICATION, EDGE_LABEL_SEMANTIC_ASSIGNMENT};

    public LineageGraphConnectorHelper(GraphTraversalSource graphTraversalSource) {
        this.g = graphTraversalSource;
    }

    /**
     * Returns a subgraph containing all root of the full graph that are connected with the queried node.
     * The queried node can be a column or table.
     *
     * @param guid The guid of the node of which the lineage is queried of. This can be a column or a table.
     *
     * @return a subgraph in an Open Lineage specific format.
     */

    public Optional<LineageVerticesAndEdges> ultimateSource(String guid, boolean includeProcesses) {
        Optional<String> edgeLabelOptional = getEdgeLabelForDataFlow(g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).next());
        if (!edgeLabelOptional.isPresent()) {
            return Optional.empty();
        }
        String edgeLabel = edgeLabelOptional.get();

        Graph sourceGraph = (Graph)
                g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).
                        until(inE(edgeLabel).count().is(0)).
                        repeat((Traversal) inE(edgeLabel).subgraph("subGraph").outV().simplePath()).
                        cap("subGraph").next();

        List<Vertex> sourcesList = g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).
                until(inE(edgeLabel).count().is(0)).
                repeat(inE(edgeLabel).outV().simplePath()).
                dedup().toList();

        return Optional.of(getCondensedLineage(guid, g, sourceGraph, getLineageVertices(sourcesList), SOURCE_CONDENSATION, includeProcesses));
    }

    /**
     * Returns a subgraph containing all leaf nodes of the full graph that are connected with the queried node.
     * The queried node can be a column or table.
     *
     * @param guid The guid of the node of which the lineage is queried of. This can be a column or table node.
     *
     * @return a subgraph in an Open Lineage specific format.
     */
    public Optional<LineageVerticesAndEdges> ultimateDestination(String guid, boolean includeProcesses) {
        Optional<String> edgeLabelOptional = getEdgeLabelForDataFlow(g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).next());
        if (!edgeLabelOptional.isPresent()) {
            return Optional.empty();
        }
        String edgeLabel = edgeLabelOptional.get();

        Graph destinationGraph = (Graph)
                g.V().has(PROPERTY_KEY_ENTITY_GUID, guid)
                        .until(outE(edgeLabel).count().is(0))
                        .repeat((Traversal) outE(edgeLabel).subgraph("subGraph").inV().simplePath())
                        .cap("subGraph").next();

        List<Vertex> destinationsList = g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).
                until(outE(edgeLabel).count().is(0)).
                repeat(outE(edgeLabel).inV().simplePath()).
                dedup().toList();

        return Optional.of(getCondensedLineage(guid, g, destinationGraph, getLineageVertices(destinationsList), DESTINATION_CONDENSATION,
                includeProcesses));
    }

    /**
     * Returns a subgraph containing all paths leading from any root node to the queried node, and all of the paths
     * leading from the queried node to any leaf nodes. The queried node can be a column or table.
     *
     * @param guid The guid of the node of which the lineage is queried of. This can be a column or a table.
     *
     * @return a subgraph in an Open Lineage specific format.
     */
    public Optional<LineageVerticesAndEdges> endToEnd(String guid, boolean includeProcesses) {
        Optional<String> edgeLabelOptional = getEdgeLabelForDataFlow(g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).next());
        if (!edgeLabelOptional.isPresent()) {
            return Optional.empty();
        }
        String edgeLabel = edgeLabelOptional.get();


        Graph endToEndGraph = (Graph)
                g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).
                        union(
                                until(inE(edgeLabel).count().is(0)).
                                        repeat((Traversal) inE(edgeLabel).subgraph("subGraph").outV().simplePath()),
                                until(outE(edgeLabel).count().is(0)).
                                        repeat((Traversal) outE(edgeLabel).subgraph("subGraph").inV().simplePath())
                        ).cap("subGraph").next();

        return Optional.of(getLineageVerticesAndEdges(endToEndGraph, includeProcesses));
    }

    /**
     * Returns a subgraph containing all root and leaf nodes of the full graph that are connected with the queried node.
     * The queried node can be a column or table.
     *
     * @param guid The guid of the node of which the lineage is queried of. This can be a column or a table.
     *
     * @return a subgraph in an Open Lineage specific format
     */
    public Optional<LineageVerticesAndEdges> sourceAndDestination(String guid, boolean includeProcesses) {
        Optional<LineageVerticesAndEdges> ultimateSourceResponse = ultimateSource(guid, includeProcesses);
        Optional<LineageVerticesAndEdges> ultimateDestinationResponse = ultimateDestination(guid, includeProcesses);

        if (ultimateSourceResponse.isPresent() && ultimateDestinationResponse.isPresent()) {
            Set<LineageVertex> sourceAndDestinationVertices = Stream.concat(ultimateSourceResponse.get().getLineageVertices().stream(),
                    ultimateDestinationResponse.get().getLineageVertices().stream()).collect(Collectors.toSet());

            Set<LineageEdge> sourceAndDestinationEdges = Stream.concat(ultimateSourceResponse.get().getLineageEdges().stream(),
                    ultimateDestinationResponse.get().getLineageEdges().stream()).collect(Collectors.toSet());

            return Optional.of(new LineageVerticesAndEdges(sourceAndDestinationVertices, sourceAndDestinationEdges));
        } else if(ultimateSourceResponse.isPresent()) {
            return ultimateSourceResponse;
        }
        else {
            if(ultimateDestinationResponse.isPresent()) {
                return ultimateDestinationResponse;
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a subgraph by navigating edges specified in {@link #glossaryTermAndClassificationEdges}, like semantic
     * assignments and various relationships between glossary terms. Classifications are included
     *
     * @param guid guid to extract vertical lineage for
     *
     * @return a subgraph in an Open Lineage specific format.
     */
    private Optional<LineageVerticesAndEdges> glossaryVerticalLineage(String guid) {

        Graph subGraph = (Graph) g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).bothE(glossaryTermAndClassificationEdges)
                .subgraph("s").cap("s").next();

        return Optional.of(getLineageVerticesAndEdges(subGraph, true));
    }

    /**
     * Returns a subgraph by navigating edges specified in {@link #relationalColumnAndClassificationEdges}, like semantic
     * assignments. Classifications are included
     *
     * @param guid guid to extract vertical lineage for
     *
     * @return a subgraph in an Open Lineage specific format.
     */
    private Optional<LineageVerticesAndEdges> relationalColumnVerticalLineage(String guid) {

        Graph subGraph = (Graph) g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).bothE(relationalColumnAndClassificationEdges)
                .subgraph("s").cap("s").next();

        return Optional.of(getLineageVerticesAndEdges(subGraph, true));
    }

    /**
     * Returns a subgraph by navigating edges specified in {@link #tabularColumnAndClassificationEdges}, like semantic
     * assignments. Classifications are included
     *
     * @param guid guid to extract vertical lineage for
     *
     * @return a subgraph in an Open Lineage specific format.
     */
    private Optional<LineageVerticesAndEdges> tabularColumnVerticalLineage(String guid ) {

        Graph subGraph = (Graph) g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).bothE(tabularColumnAndClassificationEdges)
                .subgraph("s").bothV().inE(ASSET_SCHEMA_TYPE).subgraph("s").cap("s").next();

        return Optional.of(getLineageVerticesAndEdges(subGraph, true));
    }

    /**
     * Returns a subgraph navigating the edges of interest based on target node type. For more info, check
     * {@link #glossaryVerticalLineage}, {@link #tabularColumnVerticalLineage}, {@link #relationalColumnVerticalLineage}
     *
     * @param guid guid to extract vertical lineage for
     *
     * @return a subgraph in an Open Lineage specific format
     */
    public Optional<LineageVerticesAndEdges> verticalLineage(String guid) {

        String label = g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).label().next();
        switch (label){
            case GLOSSARY_TERM:
                return glossaryVerticalLineage(guid);
            case RELATIONAL_COLUMN:
                return relationalColumnVerticalLineage(guid);
            case TABULAR_COLUMN:
                return tabularColumnVerticalLineage(guid);
            default:
                return Optional.empty();
        }
    }

    /**
     * Remove all nodes which displayname does not include the provided String. Any connected edges will also be removed.
     *
     * @param lineageVerticesAndEdges The list of vertices and edges which should be filtered on displayname.
     * @param displayNameMustContain  The substring that must be part of a node's displayname in order for that node to be returned.
     */
    void filterDisplayName(LineageVerticesAndEdges lineageVerticesAndEdges, String displayNameMustContain) {
        Set<LineageVertex> lineageVertices = lineageVerticesAndEdges.getLineageVertices();
        Set<LineageEdge> lineageEdges = lineageVerticesAndEdges.getLineageEdges();
        Set<LineageVertex> verticesToBeRemoved = new HashSet<>();
        Set<LineageEdge> edgesToBeRemoved = new HashSet<>();

        for (LineageVertex vertex : lineageVertices) {
            String nodeID = vertex.getNodeID();
            if (!vertex.getDisplayName().contains(displayNameMustContain)) {
                verticesToBeRemoved.add(vertex);
                for (LineageEdge edge : lineageEdges) {
                    if (edge.getSourceNodeID().equals(nodeID) || edge.getDestinationNodeID().equals(nodeID))
                        edgesToBeRemoved.add(edge);
                }
            }
        }
        lineageVertices.removeAll(verticesToBeRemoved);
        lineageEdges.removeAll(edgesToBeRemoved);
        lineageVerticesAndEdges.setLineageVertices(lineageVertices);
        lineageVerticesAndEdges.setLineageEdges(lineageEdges);
    }

    /**
     * * Returns a subgraph containing all root and leaf nodes of the full graph that are connected with the queried node.
     * * The queried node can be a column or table.
     *
     * @param guid             the guid of the queried node
     * @param g                graph traversal object
     * @param subGraph         subGraph containing vertices and edges to be condensed
     * @param ultimateVertices list of ultimate vertices
     * @param condensationType the type of the condensation
     * @param includeProcesses Will filter out all processes and subprocesses from the response if false.
     *
     * @return the subgraph in an Open Lineage specific format
     */
    private LineageVerticesAndEdges getCondensedLineage(String guid, GraphTraversalSource g, Graph subGraph, Set<LineageVertex> ultimateVertices,
                                                        String condensationType, boolean includeProcesses) {

        Set<LineageVertex> lineageVertices = getLineageVertices(subGraph);
        Set<LineageEdge> lineageEdges = getLineageEdges(subGraph, SOURCE_CONDENSATION.equalsIgnoreCase(condensationType));

        Vertex originalQueriedVertex = g.V().has(PROPERTY_KEY_ENTITY_GUID, guid).next();
        LineageVertex queriedVertex = abstractVertex(originalQueriedVertex);

        if (CollectionUtils.isEmpty(lineageVertices)) {
            lineageVertices = ultimateVertices;
        }

        addCondensation(lineageVertices, lineageEdges, ultimateVertices, queriedVertex, condensationType);

        condenseProcesses(includeProcesses, lineageVertices, lineageEdges);

        addColumnProperties(lineageVertices);

        return new LineageVerticesAndEdges(lineageVertices, lineageEdges);
    }

    /**
     * Map a Tinkerpop vertex to the Open Lineage format.
     *
     * @param originalVertex The vertex to be mapped.
     *
     * @return The vertex in the Open Lineage format.
     */
    private LineageVertex abstractVertex(Vertex originalVertex) {
        String nodeType = originalVertex.label();
        String nodeID = getNodeID(originalVertex);
        LineageVertex lineageVertex = new LineageVertex(nodeID, nodeType);

        if (originalVertex.property(PROPERTY_KEY_DISPLAY_NAME).isPresent()) {
            String displayName = originalVertex.property(PROPERTY_KEY_DISPLAY_NAME).value().toString();
            lineageVertex.setDisplayName(displayName);
        } else if (originalVertex.property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).isPresent()) {
            //Displayname key is stored inconsistently in the graphDB.
            String displayName = originalVertex.property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString();
            lineageVertex.setDisplayName(displayName);
        } else if (originalVertex.property(PROPERTY_KEY_LABEL).isPresent()) {
            // if no display props exist use the label. every vertex should have it
            String displayName = originalVertex.property(PROPERTY_KEY_LABEL).value().toString();
            lineageVertex.setDisplayName(displayName);
        } else {
            // if all else fails
            lineageVertex.setDisplayName("undefined");
        }

        if (originalVertex.property(PROPERTY_KEY_ENTITY_GUID).isPresent()) {
            String guid = originalVertex.property(PROPERTY_KEY_ENTITY_GUID).value().toString();
            lineageVertex.setGuid(guid);
        }
        if (NODE_LABEL_SUB_PROCESS.equals(nodeType) && originalVertex.property((PROPERTY_KEY_PROCESS_GUID)).isPresent()) {
            lineageVertex.setGuid(originalVertex.property((PROPERTY_KEY_PROCESS_GUID)).value().toString());
            lineageVertex.setNodeID(originalVertex.property((PROPERTY_KEY_PROCESS_GUID)).value().toString());

        }
        Map<String, String> properties = retrieveProperties(originalVertex);
        lineageVertex.setProperties(properties);
        return lineageVertex;
    }

    private String getNodeID(Vertex vertex) {
        String nodeID;
        if (vertex.label().equalsIgnoreCase(NODE_LABEL_SUB_PROCESS)) {
            nodeID = vertex.property(PROPERTY_KEY_PROCESS_GUID).value().toString();
        } else {
            nodeID = vertex.property(PROPERTY_KEY_ENTITY_GUID).value().toString();
        }
        return nodeID;
    }

    /**
     * Retrieve all properties of the vertex from the db and return the ones that match the whitelist. This will filter out irrelevant
     * properties that should not be returned to a UI.
     *
     * @param vertex the vertex to de mapped
     *
     * @return the filtered properties of the vertex
     */
    private Map<String, String> retrieveProperties(Vertex vertex) {
        boolean isClassificationVertex = vertex.edges(Direction.IN, EDGE_LABEL_CLASSIFICATION).hasNext();
        Map<String, String> newNodeProperties = new HashMap<>();
        Iterator<VertexProperty<Object>> originalProperties = vertex.properties();
        while (originalProperties.hasNext()) {
            Property<Object> originalProperty = originalProperties.next();
            if (immutableReturnedPropertiesWhiteList.contains(originalProperty.key()) || isClassificationVertex) {
                String newPropertyKey = originalProperty.key().
                        replace(PROPERTY_KEY_PREFIX_VERTEX_INSTANCE_PROPERTY, "").
                        replace(PROPERTY_KEY_PREFIX_ELEMENT, "");

                String newPropertyValue = originalProperty.value().toString();
                newNodeProperties.put(newPropertyKey, newPropertyValue);
            }
        }
        return newNodeProperties;
    }

    /**
     * Remove all nodes and edges from the response graph that are in between the sources and the queried node
     * and replace them by a single "condensed" node.
     *
     * @param lineageVertices  the list of all vertices returned by the Gremlin query
     * @param lineageEdges     the list of all edges returned by the Gremlin query
     * @param ultimateVertices the list of ultimate vertices
     * @param queriedVertex    the vertex which guid was queried by the user as an Open Lineage vertex object
     * @param condensationType the type of the condensed node
     */
    private void addCondensation(Set<LineageVertex> lineageVertices, Set<LineageEdge> lineageEdges,
                                 Set<LineageVertex> ultimateVertices, LineageVertex queriedVertex, String condensationType) {
        long subProcessVerticesNo = lineageVertices.stream().filter(this::isSubProcess).count();

        // only condense vertices if there is more than one process in the response graph
        if (subProcessVerticesNo > 1) {
            Set<LineageVertex> verticesToRemove = getLineageVerticesToRemove(lineageVertices, lineageEdges, ultimateVertices, queriedVertex);
            lineageVertices.removeAll(verticesToRemove);

            Set<LineageEdge> edgesToRemove = getLineageEdgesToRemove(lineageEdges, verticesToRemove);
            lineageEdges.removeAll(edgesToRemove);

            LineageVertex condensedVertex = new LineageVertex(getCondensedNodeId(condensationType), NODE_LABEL_CONDENSED);
            condensedVertex.setDisplayName(CONDENSED_NODE_DISPLAY_NAME);
            lineageVertices.add(condensedVertex);

            LineageEdge sourceEdge = new LineageEdge(EDGE_LABEL_CONDENSED, queriedVertex.getNodeID(), condensedVertex.getNodeID());
            lineageEdges.add(sourceEdge);

            ultimateVertices.forEach(ultimateVertex -> lineageEdges.add(new LineageEdge(EDGE_LABEL_CONDENSED, condensedVertex.getNodeID(),
                    ultimateVertex.getNodeID())));
        }
    }

    /**
     * Map a tinkerpop Graph object to an Open Lineage specific format.
     *
     * @param subGraph The graph to be mapped.
     *
     * @return The graph in an Open Lineage specific format.
     */
    private LineageVerticesAndEdges getLineageVerticesAndEdges(Graph subGraph, boolean includeProcesses) {
        Set<LineageVertex> lineageVertices = getLineageVertices(subGraph);
        Set<LineageEdge> lineageEdges = getLineageEdges(subGraph, false);

        condenseProcesses(includeProcesses, lineageVertices, lineageEdges);

        addColumnProperties(lineageVertices);

        return new LineageVerticesAndEdges(lineageVertices, lineageEdges);
    }

    private Set<LineageVertex> getLineageVerticesToRemove(Set<LineageVertex> lineageVertices, Set<LineageEdge> lineageEdges,
                                                          Set<LineageVertex> ultimateVertices,
                                                          LineageVertex queriedVertex) {
        Set<LineageVertex> verticesToRemove = new HashSet<>();
        lineageVertices.stream().filter(lineageVertex -> isVertexToBeCondensed(lineageVertex, queriedVertex, ultimateVertices, lineageEdges))
                .forEach(verticesToRemove::add);
        return verticesToRemove;
    }

    private boolean isVertexToBeCondensed(LineageVertex lineageVertex, LineageVertex queriedVertex, Set<LineageVertex> ultimateVertices,
                                          Set<LineageEdge> lineageEdges) {
        return !queriedVertex.getGuid().equalsIgnoreCase(lineageVertex.getGuid()) && !ultimateVertices.contains(lineageVertex);
    }

    private Set<LineageEdge> getLineageEdgesToRemove(Set<LineageEdge> lineageEdges, Set<LineageVertex> verticesToRemove) {
        Set<String> verticesToRemoveIDs = verticesToRemove.stream().map(LineageVertex::getNodeID).collect(Collectors.toSet());
        return lineageEdges.stream().filter(edge ->
                isInVertexesToRemove(verticesToRemoveIDs, edge)).collect(Collectors.toSet());
    }

    private Set<LineageEdge> getLineageEdges(Graph subGraph, boolean invertedEdges) {
        Iterator<Edge> originalEdges = subGraph.edges();
        Set<LineageEdge> lineageEdges = new HashSet<>();
        while (originalEdges.hasNext()) {
            Edge next = originalEdges.next();
            LineageEdge newLineageEdge = new LineageEdge(next.label(), getNodeID(next.outVertex()), getNodeID(next.inVertex()));
            lineageEdges.add(newLineageEdge);
        }
        return lineageEdges;
    }

    private Set<LineageVertex> getLineageVertices(Graph subGraph) {
        Iterator<Vertex> originalVertices = subGraph.vertices();
        Set<LineageVertex> lineageVertices = new LinkedHashSet<>();
        while (originalVertices.hasNext()) {
            LineageVertex newVertex = abstractVertex(originalVertices.next());
            lineageVertices.add(newVertex);
        }
        return lineageVertices;
    }

    private Set<LineageVertex> getLineageVertices(List<Vertex> vertexList) {
        Set<LineageVertex> lineageVertices = new HashSet<>();
        vertexList.forEach(vertex -> lineageVertices.add(abstractVertex(vertex)));

        return lineageVertices;
    }

    private void condenseProcesses(boolean includeProcesses, Set<LineageVertex> lineageVertices, Set<LineageEdge> lineageEdges) {
        if (!includeProcesses) {
            Set<LineageVertex> verticesToRemove = lineageVertices.stream()
                    .filter(this::isSubProcess)
                    .collect(Collectors.toSet());
            Set<String> verticesToRemoveIDs = verticesToRemove.stream().map(LineageVertex::getNodeID).collect(Collectors.toSet());
            Set<LineageEdge> edgesToRemove = lineageEdges.stream().filter(edge ->
                    isInVertexesToRemove(verticesToRemoveIDs, edge)).collect(Collectors.toSet());
            List<LineageEdge> edgesToReplaceProcesses = createEdgesThatReplaceProcesses(lineageEdges, verticesToRemoveIDs);
            lineageEdges.addAll(edgesToReplaceProcesses);
            lineageEdges.removeAll(edgesToRemove);
            lineageVertices.removeAll(verticesToRemove);
        }
    }

    private List<LineageEdge> createEdgesThatReplaceProcesses(Set<LineageEdge> lineageEdges, Set<String> verticesToRemoveNames) {
        List<LineageEdge> edgesToReplaceProcesses = new ArrayList<>();
        for (String vertexName : verticesToRemoveNames) {
            for (LineageEdge edge : lineageEdges) {
                if (edge.getDestinationNodeID().equalsIgnoreCase(vertexName)) {
                    for (LineageEdge destinationEdge : lineageEdges) {
                        if (destinationEdge.getSourceNodeID().equalsIgnoreCase(vertexName)) {
                            edgesToReplaceProcesses.add(new LineageEdge(EDGE_LABEL_CONDENSED,
                                    edge.getSourceNodeID(), destinationEdge.getDestinationNodeID()));
                        }
                    }
                }
            }
        }
        return edgesToReplaceProcesses;
    }

    private boolean isSubProcess(LineageVertex vertex) {
        return vertex.getNodeType().equalsIgnoreCase(NODE_LABEL_SUB_PROCESS);
    }

    private boolean isInVertexesToRemove(Set<String> verticesToRemoveNames, LineageEdge edge) {
        return verticesToRemoveNames.contains(edge.getSourceNodeID()) || verticesToRemoveNames.contains(edge.getDestinationNodeID());
    }

    private void addColumnProperties(Set<LineageVertex> lineageVertices) {
        if (CollectionUtils.isEmpty(lineageVertices)) {
            return;
        }

        lineageVertices.stream().filter(this::isColumn).forEach(lineageVertex -> {
            Vertex graphVertex = g.V().has(PROPERTY_KEY_ENTITY_GUID, lineageVertex.getGuid()).next();
            Object vertexId = graphVertex.id();
            Map<String, String> properties = new HashMap<>();

            switch (lineageVertex.getNodeType()) {
                case TABULAR_COLUMN:
                    properties = getDataFileProperties(g, vertexId);
                    break;
                case RELATIONAL_COLUMN:
                    properties = getRelationalTableProperties(g, vertexId);
                    break;
            }
            lineageVertex.setProperties(properties);
        });
    }

    private Map<String, String> getRelationalTableProperties(GraphTraversalSource g, Object vertexId) {
        Map<String, String> properties = new HashMap<>();

        Iterator<Vertex> tableAsset = g.V(vertexId).emit().repeat(bothE().otherV().simplePath()).times(1).or(hasLabel(RELATIONAL_TABLE));
        if (tableAsset.hasNext()) {
            properties.put(PROPERTY_KEY_TABLE_DISPLAY_NAME, tableAsset.next().property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());
        }

        Iterator<Vertex> relationalDBSchemaType =
                g.V(vertexId).emit().repeat(bothE().outV().simplePath()).times(2).or(hasLabel(RELATIONAL_DB_SCHEMA_TYPE));
        if (relationalDBSchemaType.hasNext()) {
            properties.put(PROPERTY_KEY_SCHEMA_TYPE_DISPLAY_NAME,
                    relationalDBSchemaType.next().property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());
        }

        Iterator<Vertex> deployedDatabaseSchema =
                g.V(vertexId).emit().repeat(bothE().outV().simplePath()).times(3).or(hasLabel(DEPLOYED_DB_SCHEMA_TYPE));
        if (deployedDatabaseSchema.hasNext()) {
            properties.put(PROPERTY_KEY_SCHEMA_DISPLAY_NAME,
                    deployedDatabaseSchema.next().property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());
        }

        Iterator<Vertex> database = g.V(vertexId).emit().repeat(bothE().outV().simplePath()).times(4).or(hasLabel(DATABASE));
        if (database.hasNext()) {
            properties.put(PROPERTY_KEY_DATABASE_DISPLAY_NAME,
                    database.next().property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());
        }

        Iterator<Vertex> connection = g.V(vertexId).emit().repeat(bothE().outV().simplePath()).times(5).hasLabel(CONNECTION);
        if (connection.hasNext()) {
            properties.put(PROPERTY_KEY_CONNECTION_NAME, connection.next().property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());
        }

        return properties;
    }

    private Map<String, String> getDataFileProperties(GraphTraversalSource g, Object vertexId) {
        Map<String, String> properties = new HashMap<>();

        Iterator<Vertex> tabularSchemaType = g.V(vertexId).emit().repeat(bothE().outV().simplePath()).times(1).or(hasLabel(TABULAR_SCHEMA_TYPE));
        if (tabularSchemaType.hasNext()) {
            properties.put(PROPERTY_KEY_SCHEMA_TYPE_DISPLAY_NAME,
                    tabularSchemaType.next().property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());
        }

        Iterator<Vertex> dataFileAsset = g.V(vertexId).emit().repeat(bothE().otherV().simplePath()).times(2).or(hasLabel(DATA_FILE));
        if (dataFileAsset.hasNext()) {
            Vertex dataFileVertex = dataFileAsset.next();
            properties.put(PROPERTY_KEY_TABLE_DISPLAY_NAME, dataFileVertex.property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());

            List<Vertex> folderVertices = getFolderVertices(g, dataFileVertex);
            if (CollectionUtils.isEmpty(folderVertices)) {
                return properties;
            }
            Object lastFolderVertexId = folderVertices.get(folderVertices.size() - 1).id();

            Iterator<Vertex> connection = g.V(lastFolderVertexId).emit().repeat(bothE().otherV().simplePath()).times(1).or(hasLabel(CONNECTION));
            if (connection.hasNext()) {
                properties.put(PROPERTY_KEY_CONNECTION_NAME, connection.next().property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString());
            }

            properties.put(PROPERTY_KEY_PATH, String.join("/", getFoldersPath(folderVertices)));
        }

        return properties;
    }

    private String getFoldersPath(List<Vertex> folderVertices) {
        Collections.reverse(folderVertices);
        return folderVertices.stream().map(folderVertex -> folderVertex.property(PROPERTY_KEY_INSTANCEPROP_DISPLAY_NAME).value().toString())
                .collect(Collectors.joining("/"));
    }

    private List<Vertex> getFolderVertices(GraphTraversalSource g, Vertex dataFileAsset) {
        GraphTraversal<Vertex, Vertex> fileFolders =
                g.V(dataFileAsset.id()).emit().repeat(bothE().otherV().simplePath()).until(inE(FOLDER_HIERARCHY).count().is(0)).or(hasLabel(FILE_FOLDER));
        List<Vertex> folderVertices = new ArrayList<>();
        while (fileFolders.hasNext()) {
            folderVertices.add(fileFolders.next());
        }
        return folderVertices;
    }

    private boolean isColumn(LineageVertex lineageVertex) {
        return Arrays.asList(Constants.TABULAR_COLUMN, Constants.RELATIONAL_COLUMN).contains(lineageVertex.getNodeType());
    }

    private String getCondensedNodeId(String condensationType) {
        if (SOURCE_CONDENSATION.equalsIgnoreCase(condensationType)) {
            return PROPERTY_VALUE_NODE_ID_CONDENSED_SOURCE;
        } else {
            return PROPERTY_VALUE_NODE_ID_CONDENSED_DESTINATION;
        }
    }

    private Optional<String> getEdgeLabelForDataFlow(Vertex queriedVertex) {
        String queriedNodeType = queriedVertex.label();
        switch (queriedNodeType) {
            case TABULAR_COLUMN:
            case RELATIONAL_COLUMN:
                return Optional.of(EDGE_LABEL_COLUMN_DATA_FLOW);
            case DATA_FILE:
            case RELATIONAL_TABLE:
                return Optional.of(EDGE_LABEL_TABLE_DATA_FLOW);
            default:
                return Optional.empty();
        }
    }
}
