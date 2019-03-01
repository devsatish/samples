package com.satish.spectrum.graphEngine;

public class Graph {
	 private String[] nodes;

	    private String[] links;

	    public String[] getNodes ()
	    {
	        return nodes;
	    }

	    public void setNodes (String[] nodes)
	    {
	        this.nodes = nodes;
	    }

	    public String[] getLinks ()
	    {
	        return links;
	    }

	    public void setLinks (String[] links)
	    {
	        this.links = links;
	    }

	    @Override
	    public String toString()
	    {
	        return "ClassPojo [nodes = "+nodes+", links = "+links+"]";
	    }
}
