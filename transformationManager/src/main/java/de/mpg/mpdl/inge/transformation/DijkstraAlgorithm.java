package de.mpg.mpdl.inge.transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;

public class DijkstraAlgorithm {

  private final List<TransformerEdge> edges;

  private Set<FORMAT> settledNodes;
  private Set<FORMAT> unSettledNodes;

  private Map<FORMAT, FORMAT> predecessors;
  private Map<FORMAT, TransformerEdge> predecessorEdges;
  private Map<FORMAT, Integer> distance;

  public DijkstraAlgorithm(List<FORMAT> formatList, List<TransformerEdge> transformerList) {
    // create a copy of the array so that we can operate on this array
    this.edges = new ArrayList<>(transformerList);
  }

  public void execute(FORMAT source) {
    settledNodes = new HashSet<>();
    unSettledNodes = new HashSet<>();

    distance = new HashMap<>();
    predecessors = new HashMap<>();
    predecessorEdges = new HashMap<>();
    distance.put(source, 0);
    unSettledNodes.add(source);
    while (!unSettledNodes.isEmpty()) {
      FORMAT node = getMinimum(unSettledNodes);
      settledNodes.add(node);
      unSettledNodes.remove(node);
      findMinimalDistances(node);
    }
  }

  private void findMinimalDistances(FORMAT node) {
    List<TransformerEdge> adjacentNodes = getNeighbors(node);
    for (TransformerEdge edge : adjacentNodes) {
      if (getShortestDistance(edge.getTargetFormat()) > getShortestDistance(node) + getDistance(node, edge.getTargetFormat())) {
        distance.put(edge.getTargetFormat(), getShortestDistance(node) + getDistance(node, edge.getTargetFormat()));

        predecessors.put(edge.getTargetFormat(), node);
        predecessorEdges.put(edge.getTargetFormat(), edge);

        unSettledNodes.add(edge.getTargetFormat());
      }
    }

  }

  private int getDistance(FORMAT node, FORMAT target) {
    for (TransformerEdge edge : edges) {
      if (edge.getSourceFormat().equals(node) && edge.getTargetFormat().equals(target)) {
        // All have the same weight for now
        return 1;
      }
    }
    throw new RuntimeException("Should not happen");
  }

  private List<TransformerEdge> getNeighbors(FORMAT node) {
    List<TransformerEdge> neighbors = new ArrayList<>();
    for (TransformerEdge edge : edges) {
      if (edge.getSourceFormat().equals(node) && !isSettled(edge.getTargetFormat())) {
        neighbors.add(edge);
      }
    }
    return neighbors;
  }

  private FORMAT getMinimum(Set<FORMAT> FORMATes) {
    FORMAT minimum = null;
    for (FORMAT FORMAT : FORMATes) {
      if (minimum == null) {
        minimum = FORMAT;
      } else {
        if (getShortestDistance(FORMAT) < getShortestDistance(minimum)) {
          minimum = FORMAT;
        }
      }
    }
    return minimum;
  }

  private boolean isSettled(FORMAT FORMAT) {
    return settledNodes.contains(FORMAT);
  }

  private int getShortestDistance(FORMAT destination) {
    Integer d = distance.get(destination);
    if (d == null) {
      return Integer.MAX_VALUE;
    } else {
      return d;
    }
  }

  /*
   * This method returns the path from the source to the selected target and NULL if no path exists
   */
  public List<TransformerEdge> getPath(FORMAT target) {
    LinkedList<FORMAT> path = new LinkedList<>();
    LinkedList<TransformerEdge> edgesPath = new LinkedList<>();
    FORMAT step = target;
    // check if a path exists
    if (predecessors.get(step) == null) {
      return null;
    }
    path.add(step);
    edgesPath.add(predecessorEdges.get(step));

    while (predecessors.get(step) != null) {
      step = predecessors.get(step);
      path.add(step);

      TransformerEdge predecessorEdge = predecessorEdges.get(step);
      if (predecessorEdge != null) {
        edgesPath.add(predecessorEdge);
      }

    }
    // Put it into the correct order
    Collections.reverse(path);
    Collections.reverse(edgesPath);
    return edgesPath;
  }
}
