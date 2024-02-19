package de.mpg.mpdl.inge.transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm {

  private final List<TransformerEdge> edges;

  private Set<TransformerFactory.FORMAT> settledNodes;
  private Set<TransformerFactory.FORMAT> unSettledNodes;

  private Map<TransformerFactory.FORMAT, TransformerFactory.FORMAT> predecessors;
  private Map<TransformerFactory.FORMAT, TransformerEdge> predecessorEdges;
  private Map<TransformerFactory.FORMAT, Integer> distance;

  public DijkstraAlgorithm(List<TransformerFactory.FORMAT> formatList, List<TransformerEdge> transformerList) {
    // create a copy of the array so that we can operate on this array
    this.edges = new ArrayList<>(transformerList);
  }

  public void execute(TransformerFactory.FORMAT source) {
    this.settledNodes = new HashSet<>();
    this.unSettledNodes = new HashSet<>();

    this.distance = new HashMap<>();
    this.predecessors = new HashMap<>();
    this.predecessorEdges = new HashMap<>();
    this.distance.put(source, 0);
    this.unSettledNodes.add(source);
    while (!this.unSettledNodes.isEmpty()) {
      TransformerFactory.FORMAT node = getMinimum(this.unSettledNodes);
      this.settledNodes.add(node);
      this.unSettledNodes.remove(node);
      findMinimalDistances(node);
    }
  }

  private void findMinimalDistances(TransformerFactory.FORMAT node) {
    List<TransformerEdge> adjacentNodes = getNeighbors(node);
    for (TransformerEdge edge : adjacentNodes) {
      if (getShortestDistance(edge.getTargetFormat()) > getShortestDistance(node) + getDistance(node, edge.getTargetFormat())) {
        this.distance.put(edge.getTargetFormat(), getShortestDistance(node) + getDistance(node, edge.getTargetFormat()));

        this.predecessors.put(edge.getTargetFormat(), node);
        this.predecessorEdges.put(edge.getTargetFormat(), edge);

        this.unSettledNodes.add(edge.getTargetFormat());
      }
    }

  }

  private int getDistance(TransformerFactory.FORMAT node, TransformerFactory.FORMAT target) {
    for (TransformerEdge edge : this.edges) {
      if (edge.getSourceFormat().equals(node) && edge.getTargetFormat().equals(target)) {
        // All have the same weight for now
        return 1;
      }
    }
    throw new RuntimeException("Should not happen");
  }

  private List<TransformerEdge> getNeighbors(TransformerFactory.FORMAT node) {
    List<TransformerEdge> neighbors = new ArrayList<>();
    for (TransformerEdge edge : this.edges) {
      if (edge.getSourceFormat().equals(node) && !isSettled(edge.getTargetFormat())) {
        neighbors.add(edge);
      }
    }
    return neighbors;
  }

  private TransformerFactory.FORMAT getMinimum(Set<TransformerFactory.FORMAT> FORMATes) {
    TransformerFactory.FORMAT minimum = null;
    for (TransformerFactory.FORMAT FORMAT : FORMATes) {
      if (null == minimum) {
        minimum = FORMAT;
      } else {
        if (getShortestDistance(FORMAT) < getShortestDistance(minimum)) {
          minimum = FORMAT;
        }
      }
    }
    return minimum;
  }

  private boolean isSettled(TransformerFactory.FORMAT FORMAT) {
    return this.settledNodes.contains(FORMAT);
  }

  private int getShortestDistance(TransformerFactory.FORMAT destination) {
    Integer d = this.distance.get(destination);
    if (null == d) {
      return Integer.MAX_VALUE;
    } else {
      return d;
    }
  }

  /*
   * This method returns the path from the source to the selected target and NULL if no path exists
   */
  public List<TransformerEdge> getPath(TransformerFactory.FORMAT target) {
    LinkedList<TransformerFactory.FORMAT> path = new LinkedList<>();
    LinkedList<TransformerEdge> edgesPath = new LinkedList<>();
    TransformerFactory.FORMAT step = target;
    // check if a path exists
    if (null == this.predecessors.get(step)) {
      return null;
    }
    path.add(step);
    edgesPath.add(this.predecessorEdges.get(step));

    while (null != this.predecessors.get(step)) {
      step = this.predecessors.get(step);
      path.add(step);

      TransformerEdge predecessorEdge = this.predecessorEdges.get(step);
      if (null != predecessorEdge) {
        edgesPath.add(predecessorEdge);
      }

    }
    // Put it into the correct order
    Collections.reverse(path);
    Collections.reverse(edgesPath);
    return edgesPath;
  }
}
