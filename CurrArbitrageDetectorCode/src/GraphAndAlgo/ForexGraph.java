package GraphAndAlgo;

import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;


//there are 78 unique currencies
public class ForexGraph {

    //Note: "vertex", "node" and "currency" have been used interchangably in the comments
    private int numOfCurr; //total number of currencies selected to be part of graph
    private Double[][] graph;
    private String[] currencies; //currency codes
    private String[] currenciesFull; //full names of currencies
    private double[] d; //distance of each vertex from source vertex
    private int[] prev; //tail of edge relaxed that updated a vertex

    private LinkedList<Integer> nwcycle; //the negative weight cycle in the graph if it exists
    private LinkedList<Integer> reachableFromA; //possible destination nodes
    //Note: given a source and a negative weight cycle, set A consists of nodes that have been
    // relaxed on the last iteration of Bellman Ford
    // these nodes are either part of the negative weight cycle or can reach it.
    // Therefore, if a node can be reached from A (i.e. it lies in reachableFromA),
    // it can also be reached from the negative weight cycle. Hence it is a possible destination currency.

    /**
     * Keep this method for graph test class. Not used in the actual program. Creates an empty graph given a list of vertices
     * @param vList
     */
    public ForexGraph(String[] vList){
        numOfCurr=vList.length;
        graph = new Double[numOfCurr][numOfCurr];
        currencies = new String[vList.length];
        currenciesFull = new String[vList.length];
        for(int i=0;i<vList.length;i++){
            currencies[i]=vList[i];
            currenciesFull[i]=vList[i];
        }

        d = new double[vList.length];
        prev = new int[vList.length];

    }
    /**
     * Keep this method for graph test class, not used in the actual program. Adds edge to a graph
     * @param v1
     * @param v2
     */
    public void addEdge(int v1, int v2, double weight){
        graph[v1][v2]=weight;
    }

    /**
     * Create a ForexGraph from a file and a list of currencies indices to read and write to the graph
     * @param file
     * @param currToReadIndices
     * @throws IOException
     */
    public ForexGraph(File file, ObservableList<Integer> currToReadIndices) throws IOException {
        //load file
        FileInputStream fstream = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        //skip first two lines
        br.readLine(); //timestamp
        br.readLine(); //blank

        //prepare graph
        numOfCurr =currToReadIndices.size();
        graph = new Double[numOfCurr][numOfCurr];
        currencies = new String[numOfCurr];
        currenciesFull = new String[numOfCurr];

        //read data
        int base=0; //base will increment from 0 to numOfCurr. This represents the currency to be read
        int curr=0; //curr indicates the current currency being considered as base. It ranges from 0 to 77

        while(base<numOfCurr){
            if(currToReadIndices.get(base)!=curr){ //if the current currency is not the same as the currency to be read
                skipCurrency(br); //skip the whole of current currency (by simply calling br.readline() 78 times)
                curr++; //increment currency counter
            }
            else{ //if the current currency is the same as the currency to be read, read and store it

                //now we are within a currency section, base is fixed, the "other" currency is changing
                //we will now populate the row of a graph

                int currInner=0; //possible other currencies counter
                int other=0; //represents "other" currency to be read

                //the first line is also used to store currency code and currency name, we need that for reporting the NWC cycle
                String[] line1 = br.readLine().split(",");
                currencies[base]=line1[1];
                currenciesFull[base]=line1[0];

                //now read the first entry if "0" is included in the indices to be selected, otherwise skip
                if(currToReadIndices.get(other)==0){
                    if(base==other||line1[3].equals("-"))
                        graph[base][other]=null;
                    else
                        graph[base][other]=-Math.log(Double.valueOf(line1[3]));
                        other++;
                }
                currInner++;
                //else skip first line and move on
                while(other<numOfCurr){ //other ranges from 1 to numOfCurr
                    if(currToReadIndices.get(other)!=currInner){ //if current inner curr doesnt match other, skip it
                        //skip item
                        br.readLine();
                        currInner++;
                    }
                    else{ //if current inner curr matches other, read it
                        String linePart = br.readLine().split(",")[3];
                        if(base==other||linePart.equals("-"))
                            graph[base][other]=null;
                        else
                            graph[base][other]=-Math.log(Double.valueOf(linePart));
                            currInner++;
                            other++;
                    }
                }

                moveToEndOfCurrency(currInner,br); //once reached the last inner currency to read in this part,
                                                   // move to the end of the section
                curr++; //increment current currency counter, because you have finished with a whole currency section
                base++; //since you have stored a whole row in the graph, increment currency to be read counter
                }
            }

        in.close();
    }

    /**
     * Helper method for the constructor ForexGraph(String file, ObservableList<Integer> currToReadIndices) throws IOException
     * @param lastRead
     * @param br
     * @throws IOException
     */
    private void moveToEndOfCurrency(int lastRead, BufferedReader br) throws IOException {
        for(int i=lastRead;i<78;i++){
            br.readLine();
        }
    }

    /**
     * Helper method for the constructor ForexGraph(String file, ObservableList<Integer> currToReadIndices) throws IOException
     * @param br
     * @throws IOException
     */
    private void skipCurrency(BufferedReader br) throws IOException {
        for(int i=0;i<78;i++){
            br.readLine();
        }
    }

    /**
     * Used by Screen2Controller's method createPressed(). Remove a edge from the graph.
     * @param base
     * @param other
     */
    public void removeEdge(int base, int other){
        graph[base][other]=null;
    }

    /**
     * Used by Screen2Controller's initialize method. Checks if there is an edge on the graph.
     * @param base
     * @param other
     */
    public boolean hasEdge(int base, int other){
        return graph[base][other]!=null;
    }

    /**
     * Method for debugging. Displays graph. Called in Screen1Controller. Can be deleted.
     */
    public void displayGraph(){
        for(int i = 0; i < numOfCurr; i++){
            System.out.print(currencies[i]+": ");
            for(int j = 0; j< numOfCurr; j++)
                System.out.print(graph[i][j]+" ");
            System.out.print("\n");
        }
    }

    /**
     * Detects existence of any negative weight cycle reachable from given source currency
     * @param source
     * @return true if such a negative weight cycle exists, else return false
     */
    public boolean detectNWCycle(int source){
        if(source==-1){
            //System.out.println("Currency does not exist!");
            return false;
        }
        initialize(source);

        //displayDPREV();

        // |V-1| iterations of Bellman Ford
        for(int i=1; i<=currencies.length-1;i++){
            //System.out.println(i+"th iteration of Bellman Ford");
            //relax each edge
            for(int c1=0;c1<currencies.length;c1++){
                for(int c2=0;c2<currencies.length;c2++){
                    if(graph[c1][c2]!=null){
                        //System.out.println("Looking at edge: ("+currencies[c1]+", "+currencies[c2]+")");
                        relax(c1,c2);
                    }
                }
            }
            //System.out.println();
        }

        int relaxedNode =-1;

        Set<Integer> A = new LinkedHashSet<Integer>();
        // Vth iteration of Bellman Ford
        // Relax each edge. Each edge relaxed on this iteration will be added to a set A
        // Finally add all items from A to Q.
        //System.out.println(currencies.length+"th iteration of Bellman Ford");
        for(int c1=0;c1<currencies.length;c1++){
            for(int c2=0;c2<currencies.length;c2++){
                if(graph[c1][c2]!=null) {
                    //System.out.println("Looking at edge: ("+currencies[c1]+", "+currencies[c2]+")");
                    if (d[c2] > (d[c1] + graph[c1][c2])) {
                        //NEGATIVE WEIGHT CYCLE EXISTS
                        if(relaxedNode==-1){
                            relaxedNode=c2; //get any node (in this case the first), that is relaxed during this last iteration of Bellman Ford
                        }
                        relax(c1, c2);

                        A.add(c2);
                        // Set A consists of elements that are either in the negative weight cycle or the negative weight cycle is reachable from them
                        ////System.out.println("A: "+A);
                    }
                }
            }
        }
        //System.out.println();
        //System.out.println("A: "+A);
        if(relaxedNode!=-1){
            Queue<Integer> Q = new LinkedList<>();
            for (Integer a : A) {
                Q.add(a);
            }
            //System.out.println("Q: "+Q);
            BFS_populateReachableFromA(Q);
            // BFS_populateReachableFromA will be performed starting from Q to find all node reachable from A. If the destination is reachable from A then this will mean that infinite arbitrage is possible.

            findNegCycle(relaxedNode);
            return true;
        }

        else {
            //System.out.println("No negative weight nwcycle found.");
            // No negative weight cycle reachable from source exists
            return false;
        }
    }

    /**
     * Helper method for detectNWCycle(String sourceStr). Initializes the vectors d and prev.
     * @param s
     */
    private void initialize(int s){
        d = new double[numOfCurr]; //distances
        prev = new int[numOfCurr]; //predecessor;

        //for each vertex in G, initialize distance of each vertex as 0 and the predecessor as null
        for(int c=0; c<currencies.length;c++){
            d[c]=Double.POSITIVE_INFINITY;
            prev[c]=-1;
        }
        //set distance of source to be 0
        d[s]=0;
    }

    /**
     * Helper for Bellman Ford Algo
     * @param c1
     * @param c2
     */
    private void relax(int c1, int c2){
        ////System.out.println("edge relaxed: ("+c1+","+c2+")");
        // if current distance of the vertex c2 is greater than the sum of distance c1 and the weight of (c1,c2), relax and update prev of c2
        if( d[c2] > (d[c1] + graph[c1][c2]) ){
            d[c2] = d[c1] + graph[c1][c2];
            prev[c2]=c1;
            //System.out.println("Relaxed edge: ("+currencies[c1]+", "+currencies[c2]+")");
            //displayDPREV();
        }
    }

    /**
     * Given a destination currency, determines whether or not infinite arbitrage is possible.
     * @param destination
     * @return true if infinite arbitrage is possible, else return false.
     */
    public boolean infiniteArbitragePossible(int destination){
        ////System.out.println("Q: "+Q);
        //if(reachableFromA==null)
          //  BFS_populateReachableFromA();
        return reachableFromA.contains(destination);
        //return isInReachableFromA(destination);
    }


    public void findNegCycle(int c1){
        //System.out.println("\nLocate negative weight cycle");

        nwcycle = new LinkedList<>(); //negative weight nwcycle
        //displayDPREV();

        int x = prev[c1];
        //System.out.println("Begin from prev of "+currencies[c1]+": "+currencies[x]);
        //System.out.println("Follow prev v times: ");
        for(int i=0;i<currencies.length;i++){//|V| times
            x=prev[x];
            //System.out.print("prev: "+currencies[x]+"; ");
        }
        //System.out.println();
        //System.out.println("Now certainly in nwc");
        //System.out.println("Add "+currencies[x]+" to nwc");
        //System.out.println("And follow prev until "+currencies[x]+" repeats.");
        nwcycle = new LinkedList<>();
        int y=x;
        nwcycle.addFirst(x);
        x=prev[x];
        //System.out.print("prev: "+currencies[x]+"; ");
        nwcycle.addFirst(x);
        while(y!=x){
            x=prev[x];
            //System.out.print("prev: "+currencies[x]+"; ");
            nwcycle.addFirst(x);
        }
        //System.out.println("\nNWC: "+nwcycle);

    }

    public String[] getCurrenciesFull(){
        return currenciesFull;
    }
    public String[] getCurrencies(){
        return currencies;
    }
    public LinkedList<Integer> getNWCycle(){
        return nwcycle;
    }

    public void BFS_populateReachableFromA(Queue<Integer> Q){
        //System.out.println();
        //System.out.println("Doing BFS: ");
        reachableFromA = new LinkedList<>();
        while(!Q.isEmpty()){
            int dequed = Q.poll();
            //System.out.println("dequed vertex is "+currencies[dequed]);
            for(int j=0; j<currencies.length;j++){
                //int nhbr;
                //if(graph[dequed][j]==null||graph[dequed][j]==0) nhbr=-1; //if nhbr is -1 this means not nhbr (i.e. no edge exists)
                //else nhbr=j;
                //if(nhbr!=-1&&!reachableFromA.contains(nhbr)){
                if(graph[dequed][j]!=null&&!reachableFromA.contains(j)){
                    //System.out.print(currencies[j]+" is added to reachableFromA");
                    if(!Q.contains(j)){
                        Q.add(j);
                        //System.out.print(" and the Q.");
                    }
                    //System.out.println();
                    reachableFromA.add(j);
                }
            }
        }
        //System.out.println();
        //System.out.println("ReachableFromA: "+reachableFromA);
    }


    /**
     * Call when source currency is changed
     */
    public void resetGraph(){
        d=null;
        prev=null;
        nwcycle=null;
        reachableFromA=null;
        //Q=null;
    }

    /**
     * Method for debugging. Displays the d and prev vectors.
     */
    private void displayDPREV(){
        System.out.print("D: ");
        for(int i=0;i<numOfCurr;i++){
            System.out.print(d[i]+" ");
        }
        System.out.print("\nPREV: ");
        for(int i=0;i<numOfCurr;i++){
            System.out.print(prev[i]+" ");
        }
        System.out.println();
    }
}
